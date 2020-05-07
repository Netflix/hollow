/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.api.codegen;

import com.netflix.hollow.api.objects.delegate.HollowListCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;
import com.netflix.hollow.api.objects.delegate.HollowMapCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.api.objects.delegate.HollowMapLookupDelegate;
import com.netflix.hollow.api.objects.delegate.HollowSetCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.delegate.HollowSetLookupDelegate;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class containing convenience methods for the {@link HollowAPIGenerator}.  Not intended for external consumption.
 */
public class HollowCodeGenerationUtils {

    private static final Set<String> PRIMITIVE_TYPES = new HashSet<>();
    private static final Map<String,String> DEFAULT_CLASS_NAME_SUBSTITUTIONS = new HashMap<String,String>();
    private static final Map<String,String> AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS = new HashMap<String,String>();

    static {
        for(Class<?> clzz : Arrays.asList(Boolean.class, Integer.class, Long.class, Float.class, Double.class, String.class)) {
            PRIMITIVE_TYPES.add(clzz.getSimpleName());
        }

        DEFAULT_CLASS_NAME_SUBSTITUTIONS.put("String", "HString");
        DEFAULT_CLASS_NAME_SUBSTITUTIONS.put("Integer", "HInteger");
        DEFAULT_CLASS_NAME_SUBSTITUTIONS.put("Long", "HLong");
        DEFAULT_CLASS_NAME_SUBSTITUTIONS.put("Float", "HFloat");
        DEFAULT_CLASS_NAME_SUBSTITUTIONS.put("Double", "HDouble");
        DEFAULT_CLASS_NAME_SUBSTITUTIONS.put("Boolean", "HBoolean");
        DEFAULT_CLASS_NAME_SUBSTITUTIONS.put("Object", "HObject");

        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("AbstractMethodError", "HAbstractMethodError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Appendable", "HAppendable");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("ArithmeticException", "HArithmeticException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("ArrayIndexOutOfBoundsException", "HArrayIndexOutOfBoundsException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("ArrayStoreException", "HArrayStoreException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("AssertionError", "HAssertionError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("AutoCloseable", "HAutoCloseable");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Boolean", "HBoolean");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("BootstrapMethodError", "HBootstrapMethodError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Byte", "HByte");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("CharSequence", "HCharSequence");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Character", "HCharacter");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Class", "HClass");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("ClassCastException", "HClassCastException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("ClassCircularityError", "HClassCircularityError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("ClassFormatError", "HClassFormatError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("ClassLoader", "HClassLoader");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("ClassNotFoundException", "HClassNotFoundException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("ClassValue", "HClassValue");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("CloneNotSupportedException", "HCloneNotSupportedException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Cloneable", "HCloneable");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Comparable", "HComparable");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Compiler", "HCompiler");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Deprecated", "HDeprecated");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Double", "HDouble");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Enum", "HEnum");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("EnumConstantNotPresentException", "HEnumConstantNotPresentException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Error", "HError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Exception", "HException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("ExceptionInInitializerError", "HExceptionInInitializerError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Float", "HFloat");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("IllegalAccessError", "HIllegalAccessError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("IllegalAccessException", "HIllegalAccessException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("IllegalArgumentException", "HIllegalArgumentException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("IllegalMonitorStateException", "HIllegalMonitorStateException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("IllegalStateException", "HIllegalStateException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("IllegalThreadStateException", "HIllegalThreadStateException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("IncompatibleClassChangeError", "HIncompatibleClassChangeError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("IndexOutOfBoundsException", "HIndexOutOfBoundsException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("InheritableThreadLocal", "HInheritableThreadLocal");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("InstantiationError", "HInstantiationError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("InstantiationException", "HInstantiationException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Integer", "HInteger");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("InternalError", "HInternalError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("InterruptedException", "HInterruptedException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Iterable", "HIterable");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("LinkageError", "HLinkageError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Long", "HLong");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Math", "HMath");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("NegativeArraySizeException", "HNegativeArraySizeException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("NoClassDefFoundError", "HNoClassDefFoundError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("NoSuchFieldError", "HNoSuchFieldError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("NoSuchFieldException", "HNoSuchFieldException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("NoSuchMethodError", "HNoSuchMethodError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("NoSuchMethodException", "HNoSuchMethodException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("NullPointerException", "HNullPointerException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Number", "HNumber");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("NumberFormatException", "HNumberFormatException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Object", "HObject");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("OutOfMemoryError", "HOutOfMemoryError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Override", "HOverride");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Package", "HPackage");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Process", "HProcess");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("ProcessBuilder", "HProcessBuilder");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Readable", "HReadable");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("ReflectiveOperationException", "HReflectiveOperationException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Runnable", "HRunnable");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Runtime", "HRuntime");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("RuntimeException", "HRuntimeException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("RuntimePermission", "HRuntimePermission");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("SafeVarargs", "HSafeVarargs");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("SecurityException", "HSecurityException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("SecurityManager", "HSecurityManager");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Short", "HShort");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("StackOverflowError", "HStackOverflowError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("StackTraceElement", "HStackTraceElement");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("StrictMath", "HStrictMath");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("String", "HString");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("StringBuffer", "HStringBuffer");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("StringBuilder", "HStringBuilder");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("StringIndexOutOfBoundsException", "HStringIndexOutOfBoundsException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("SuppressWarnings", "HSuppressWarnings");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("System", "HSystem");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Thread", "HThread");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("ThreadDeath", "HThreadDeath");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("ThreadGroup", "HThreadGroup");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("ThreadLocal", "HThreadLocal");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Throwable", "HThrowable");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("TypeNotPresentException", "HTypeNotPresentException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("UnknownError", "HUnknownError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("UnsatisfiedLinkError", "HUnsatisfiedLinkError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("UnsupportedClassVersionError", "HUnsupportedClassVersionError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("UnsupportedOperationException", "HUnsupportedOperationException");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("VerifyError", "HVerifyError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("VirtualMachineError", "HVirtualMachineError");
        AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.put("Void", "HVoid");
    }

    public static String typeAPIClassname(String typeName) {
        return uppercase(typeName) + "TypeAPI";
    }

    public static String hollowFactoryClassname(String typeName) {
        return substituteInvalidChars(uppercase(typeName)) + "HollowFactory";
    }

    public static String hollowObjectProviderName(String typeName) {
        return substituteInvalidChars(lowercase(typeName)) + "Provider";
    }

    public static String hollowImplClassname(String typeName, String classPostfix,
            boolean useAggressiveSubstitutions, boolean useHollowPrimitives) {
        String classname = substituteInvalidChars(uppercase(typeName));
        if (!useHollowPrimitives && !"".equals(classPostfix)) {
            // skip substitutions here to preserve legacy behaviour
            return classname + classPostfix;
        }
        String sub = useAggressiveSubstitutions ?
            AGGRESSIVE_CLASS_NAME_SUBSTITUTIONS.get(classname) :
            DEFAULT_CLASS_NAME_SUBSTITUTIONS.get(classname);
        return sub == null ? classname + classPostfix : sub;
    }

    public static String delegateInterfaceName(String typeName) {
        return substituteInvalidChars(uppercase(typeName)) + "Delegate";
    }

    public static String delegateInterfaceName(HollowSchema schema) {
        if(schema instanceof HollowObjectSchema)
            return delegateInterfaceName(schema.getName());
        if(schema instanceof HollowListSchema)
            return HollowListDelegate.class.getSimpleName();
        if(schema instanceof HollowSetSchema)
            return HollowSetDelegate.class.getSimpleName();
        if(schema instanceof HollowMapSchema)
            return HollowMapDelegate.class.getSimpleName();
        throw new UnsupportedOperationException("What kind of schema is a " + schema.getClass().getSimpleName() + "?");
    }

    public static String delegateCachedImplName(String typeName) {
        return substituteInvalidChars(uppercase(typeName)) + "DelegateCachedImpl";
    }

    public static String delegateCachedClassname(HollowSchema schema) {
        if(schema instanceof HollowObjectSchema)
            return delegateCachedImplName(schema.getName());
        if(schema instanceof HollowListSchema)
            return HollowListCachedDelegate.class.getSimpleName();
        if(schema instanceof HollowSetSchema)
            return HollowSetCachedDelegate.class.getSimpleName();
        if(schema instanceof HollowMapSchema)
            return HollowMapCachedDelegate.class.getSimpleName();
        throw new UnsupportedOperationException("What kind of schema is a " + schema.getClass().getSimpleName() + "?");
    }

    public static String delegateLookupImplName(String typeName) {
        return substituteInvalidChars(uppercase(typeName)) + "DelegateLookupImpl";
    }

    public static String delegateLookupClassname(HollowSchema schema) {
        if(schema instanceof HollowObjectSchema)
            return delegateLookupImplName(schema.getName());
        if(schema instanceof HollowListSchema)
            return HollowListLookupDelegate.class.getSimpleName();
        if(schema instanceof HollowSetSchema)
            return HollowSetLookupDelegate.class.getSimpleName();
        if(schema instanceof HollowMapSchema)
            return HollowMapLookupDelegate.class.getSimpleName();
        throw new UnsupportedOperationException("What kind of schema is a " + schema.getClass().getSimpleName() + "?");
    }

    public static String lowercase(String str) {
        if(str == null || str.length() == 0)
            return str;

        StringBuilder builder = new StringBuilder();

        builder.append(str.substring(0, 1).toLowerCase());
        builder.append(str.substring(1));

        return builder.toString();
    }

    public static String uppercase(String str) {
        return upperFirstChar(str);
    }

    public static String upperFirstChar(String str) {
        if(str == null || str.length() == 0)
            return str;

        StringBuilder builder = new StringBuilder();

        builder.append(str.substring(0, 1).toUpperCase());
        builder.append(str.substring(1));

        return builder.toString();
    }

    public static String substituteInvalidChars(String str) {
        str = str.replace(' ', '_');
        str = str.replace('.', '_');
        return str;
    }

    public static String getJavaBoxedType(FieldType fieldType) {
        switch(fieldType) {
            case BOOLEAN:
                return "Boolean";
            case BYTES:
                return "byte[]";
            case DOUBLE:
                return "Double";
            case FLOAT:
                return "Float";
            case LONG:
                return "Long";
            case INT:
            case REFERENCE:
                return "Integer";
            case STRING:
                return "String";
        }
        throw new IllegalArgumentException("Java boxed type is not known for FieldType." + fieldType.toString());
    }

    public static String getJavaScalarType(FieldType fieldType) {
        switch(fieldType) {
            case BOOLEAN:
                return "boolean";
            case BYTES:
                return "byte[]";
            case DOUBLE:
                return "double";
            case FLOAT:
                return "float";
            case LONG:
                return "long";
            case INT:
            case REFERENCE:
                return "int";
            case STRING:
                return "String";
        }
        throw new IllegalArgumentException("Java scalar type is not known for FieldType." + fieldType.toString());
    }

    private static final Set<String> booleanMethodPrefixes = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "is", "has", "do", "should", "was", "contains", "enable", "disable", "deserializeFrom")));
    public static Set<String> getBooleanMethodPrefixes() { return booleanMethodPrefixes; }

    /**
     * Rules: prepend "deserializeFrom" / "is" + upper case first char of field name
     *
     * boolean/Boolean field:
     *    - has a boolean prefix (@see {@link #booleanMethodPrefixes}), just return it; otherwise, prepend "deserializeFrom" + upper case first char
     *
     *      boolean isPrimary - isPrimary()
     *      boolean hasStreams - hasStreams()
     *      boolean playable - getPlayable()
     *      boolean value - getValue()
     *
     * other field type: prepend "deserializeFrom" + upper case first char
     *
     *      String title - getTitle()
     *
     * @param fieldName
     *            name of field
     * @param clazz
     *            type of field
     * @return accessor method name
     */
    public static String generateAccessortMethodName(String fieldName, Class<?> clazz) {
        String prefix = "deserializeFrom";
        if (boolean.class.equals(clazz) || Boolean.class.equals(clazz)) {
            for (String booleanPrefix : booleanMethodPrefixes) {
                if (fieldName.startsWith(booleanPrefix) && fieldName.length() > booleanPrefix.length()) {
                    char firstCharAfterBooleanPrefix = fieldName.charAt(booleanPrefix.length());
                    if (Character.isUpperCase(firstCharAfterBooleanPrefix)) {
                        return fieldName;
                    }
                }
            }
        }

        return substituteInvalidChars(prefix + uppercase(fieldName));
    }

    public static String generateBooleanAccessorMethodName(String fieldName, boolean useBooleanFieldErgonomics) {
        return useBooleanFieldErgonomics ? generateAccessortMethodName(fieldName, boolean.class) : "deserializeFrom" + uppercase(fieldName);
    }

    /**
     * Convert field path into Param name
     *
     * Eg:
     *  - Actor {@literal->} actor
     *  - Actor.name {@literal->} actorName
     *
     * @param fieldPath the field path
     * @return the param name
     */
    public static String normalizeFieldPathToParamName(String fieldPath) {
        String result = null;
        if (fieldPath.contains(".")) {
            String[] parts = fieldPath.split("\\.");
            StringBuilder sb = new StringBuilder();
            sb.append(lowercase(parts[0]));
            for (int i = 1; i < parts.length; i++) {
                sb.append(uppercase(parts[i]));
            }
            result = sb.toString();
        } else {
            result = lowercase(fieldPath);
        }

        if (result.endsWith("!")) {
            return result.substring(0, result.length() - 1);
        }
        return result;
    }

    public static boolean isPrimitiveType(String type) {
        return PRIMITIVE_TYPES.contains(type);
    }

    public static Set<String> getPrimitiveTypes(Collection<HollowSchema> schemaList) {
        Set<String> primitiveTypes = new HashSet<>();
        for (HollowSchema schema : schemaList) {
            String type = schema.getName();
            if (!isPrimitiveType(type)) continue;

            primitiveTypes.add(type);
        }
        return primitiveTypes;
    }

    public static boolean isCollectionType(String schemaName, HollowDataset dataset) {
        return dataset.getSchema(schemaName).getSchemaType() != HollowSchema.SchemaType.OBJECT;
    }
}
