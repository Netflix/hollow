package com.netflix.hollow.jsonadapter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.netflix.hollow.api.objects.HollowObject;
import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Jackson module allowing serialization of generated {@link HollowObject} beans.
 */
public class HollowModule extends SimpleModule {
    private boolean preservePropertyOrder;
    private boolean internMemoizable;

    /**
     * Construct a {@link HollowModule} with optional features disabled.
     */
    public HollowModule() {
    }

    /**
     * Construct a {@link HollowModule} configuring optional features.
     *
     * @param preservePropertyOrder preserve the originally declared property order,
     * rather than using the undefined {@link Class#getDeclaredMethods()} order.
     * @param internMemoizable intern deserialized objects that have the {@code __assigned_ordinal} field defined. The interned objects are strongly referenced and will live for the lifetime of the
     * module, and the POJO memoization optimization only works for a single producer cycle, so ensure the {@link com.fasterxml.jackson.databind.ObjectMapper} is not referenced beyond one cycle
     */
    public HollowModule(boolean preservePropertyOrder, boolean internMemoizable) {
        this.preservePropertyOrder = preservePropertyOrder;
        this.internMemoizable = internMemoizable;
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.addBeanSerializerModifier(new HollowObjectBeanSerializerModifier(preservePropertyOrder));
        context.addBeanDeserializerModifier(new HollowModelBeanDeserializerModifier(internMemoizable));
    }

    private class HollowModelBeanDeserializerModifier extends BeanDeserializerModifier {
        private boolean internMemoizable;

        HollowModelBeanDeserializerModifier(boolean internMemoizable) {
            this.internMemoizable = internMemoizable;
        }

        @Override
        public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
            if (deserializer instanceof BeanDeserializer) {
                Set<String> propertyNames = new HashSet<>();
                BeanDeserializer beanDeserializer = ((BeanDeserializer) deserializer);
                beanDeserializer.properties().forEachRemaining(property -> propertyNames.add(property.getName()));
                if (propertyNames.contains("__assignedOrdinal") && internMemoizable) {
                    return new InterningBeanDeserializer(beanDeserializer);
                }
            }
            return deserializer;
        }
    }

    /**
     * Simple interning bean deserializer. Guava's Interner is more memory efficient, but this avoid additional dependencies.
     */
    private class InterningBeanDeserializer extends BeanDeserializer {
        // We've got no way of getting an entry by key, so we use the bean object as both the key and the value so we can do this in a single round trip
        private Map<Object, Object> map = new ConcurrentHashMap<>();

        InterningBeanDeserializer(BeanDeserializerBase src) {
            super(src);
        }

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            Object bean = super.deserialize(p, ctxt);
            Object existing = map.get(bean);
            if (existing != null) {
                return existing;
            }
            Object conflict = map.putIfAbsent(bean, bean);
            return conflict == null ? bean : conflict;
        }
    }

    private class HollowObjectBeanSerializerModifier extends BeanSerializerModifier {
        private boolean preservePropertyOrder;

        HollowObjectBeanSerializerModifier(boolean preservePropertyOrder) {
            this.preservePropertyOrder = preservePropertyOrder;
        }

        @Override
        public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
            JavaType javaType = beanDesc.getType();
            if (javaType.isTypeOrSubTypeOf(HollowObject.class)) {
                Set<String> methodNames = beanProperties.stream()
                        .map(bean -> bean.getMember().getName())
                        .collect(Collectors.toSet());
                return beanProperties.stream().filter(bean -> {
                    // When there are properties that start the same, for instance foo and fooBar, the property name becomes foo and foobar, so we compare the original method names
                    AnnotatedMethod method = (AnnotatedMethod) bean.getMember();
                    String methodName = method.getName();
                    boolean isDeclaredByBean = javaType.getRawClass() == bean.getMember().getDeclaringClass();
                    boolean isHollowReference = methodName.endsWith("HollowReference");
                    boolean isAssignedOrdinal = methodName.contains("__assignedOrdinal");
                    // Ergonomic classes have different default values for null boxed types, so we want to use the boxed accessor instead
                    boolean hasBoxedVariant = methodNames.contains(methodName + "Boxed");
                    return isDeclaredByBean && !(isHollowReference || isAssignedOrdinal || hasBoxedVariant);
                }).map(bean -> {
                    AnnotatedMethod method = (AnnotatedMethod) bean.getMember();
                    String methodName = method.getName();
                    if (methodName.endsWith("Boxed")) {
                        return bean.rename(new NameTransformer() {
                            @Override
                            public String transform(String name) {
                                int index = name.toLowerCase().indexOf("boxed");
                                return name.substring(0, index);
                            }

                            @Override
                            public String reverse(String transformed) {
                                return methodName;
                            }
                        });
                    }
                    return bean;
                }).collect(Collectors.toList());
            }
            return beanProperties;
        }

        @Override
        public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
            if (beanDesc.getType().isTypeOrSubTypeOf(HollowObject.class)) {
                List<PropertyWriter> propertyWriters = new ArrayList<>();
                serializer.properties().forEachRemaining(propertyWriters::add);
                if (propertyWriters.size() == 1) {
                    BeanPropertyWriter beanPropertyWriter = (BeanPropertyWriter) propertyWriters.get(0);
                    if (!beanPropertyWriter.getPropertyType().isAssignableFrom(HollowObject.class)) {
                        return new SinglePropertyWriterSerializer(beanPropertyWriter);
                    }
                }
            }
            return serializer;
        }

        @Override
        public JsonSerializer<?> modifyKeySerializer(SerializationConfig config, JavaType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
            if (serializer.handledType() == Object.class) {
                //noinspection unchecked
                return new SinglePropertyWriterKeySerializer((JsonSerializer<Object>) serializer);
            }
            return serializer;
        }

        @Override
        public List<BeanPropertyWriter> orderProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
            if (preservePropertyOrder) {
                List<String> orderedBeanPropertyNames = declaredOrderBeanPropertyNames(beanDesc);
                beanProperties.sort(Comparator.comparingInt(o -> orderedBeanPropertyNames.indexOf(o.getName())));
            }
            return beanProperties;
        }

        /**
         * Unfortunately, {@link Class#getDeclaredMethods()} doesn't return methods in their declared order. {@link HollowObject#getSchema()} would allow us to get at the field ordering,
         * however that would couple the module to supporting specific instance of an API, so we walk the class methods instead.
         */
        private List<String> declaredOrderBeanPropertyNames(BeanDescription beanDesc) {
            try {
                Class<?> rawClass = beanDesc.getType().getRawClass();
                String path = rawClass.getName().replace(".", "/") + ".class";
                try (InputStream inputStream = rawClass.getClassLoader().getResourceAsStream(path)) {
                    List<String> beanPropertyNames = new ArrayList<>();
                    ClassReader classReader = new ClassReader(inputStream);
                    classReader.accept(new ClassVisitor(Opcodes.ASM6) {
                        @Override
                        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                            if (name.startsWith("get")) {
                                String propertyName = Introspector.decapitalize(name.substring(3));
                                beanPropertyNames.add(propertyName);
                            }
                            return null;
                        }
                    }, ClassReader.EXPAND_FRAMES);
                    return beanPropertyNames;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Serializer for {@link HollowObject} that auto-expands single property non-reference types.
     */
    private static class SinglePropertyWriterSerializer extends JsonSerializer<Object> {
        private BeanPropertyWriter beanPropertyWriter;

        SinglePropertyWriterSerializer(BeanPropertyWriter beanPropertyWriter) {
            this.beanPropertyWriter = beanPropertyWriter;
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            try {
                beanPropertyWriter.serializeAsElement(value, gen, serializers);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    /**
     * Key serializer to accompany {@link SinglePropertyWriterSerializer}, so that when a single property can be written, it's used as the field value.
     */
    private static class SinglePropertyWriterKeySerializer extends JsonSerializer {
        private JsonSerializer<Object> defaultSerializer;

        SinglePropertyWriterKeySerializer(JsonSerializer<Object> defaultSerializer) {
            this.defaultSerializer = defaultSerializer;
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            Object propertyValue = value;
            JsonSerializer<Object> serializer = serializers.findValueSerializer(value.getClass());
            if (serializer instanceof SinglePropertyWriterSerializer) {
                propertyValue = ((SinglePropertyWriterSerializer) serializer)
                        .beanPropertyWriter
                        .getMember()
                        .getValue(value);
            }
            defaultSerializer.serialize(propertyValue, gen, serializers);
        }
    }
}
