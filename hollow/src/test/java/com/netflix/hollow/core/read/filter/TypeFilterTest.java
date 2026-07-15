package com.netflix.hollow.core.read.filter;

import static com.netflix.hollow.core.read.filter.TypeFilter.newTypeFilter;
import static com.netflix.hollow.core.schema.HollowSchema.SchemaType.OBJECT;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

public class TypeFilterTest {
    private static final Collection<Object[]> typeCases = asList(
            // includeAll() is the default
            c("include all: object",      l(Charlie.class),             f -> f, l("Charlie")),
            c("include all: subclass",    l(Beta.class),                f -> f, l("Beta", "Charlie", "Long")),
            c("include all: hierarchy",   l(Alpha.class),               f -> f, l("Alpha", "Beta", "Charlie", "Long")),
            c("include all: disjoint",    l(Charlie.class, Echo.class), f -> f, l("Charlie", "Echo")),
            c("include all: collections", l(Omega.class),               f -> f,
                    l("Omega",
                      "ListOfLE", "LE", "LERef",
                      "SetOfSE", "SE", "SERef",
                      "MapOfKToV", "K", "KRef", "V", "VRef")),

            c("exclude all: hierarchy",   l(Alpha.class), f -> f.excludeAll(), none()),
            c("exclude all: collections", l(Omega.class), f -> f.excludeAll(), none()),

            c("include: hierarchy",   l(Alpha.class), f -> f.excludeAll().include("Alpha"), l("Alpha")),
            c("include: collections", l(Omega.class), f -> f.excludeAll().include("Omega"), l("Omega")),

            c("recursive include: hierarchy",
                    l(Alpha.class),
                    f -> f.excludeAll()
                          .includeRecursive("Alpha"),
                    l("Alpha", "Beta", "Charlie", "Long")),
            c("recursive include: collections",
                    l(Omega.class),
                    f -> f.excludeAll()
                          .includeRecursive("Omega"),
                    l("Omega",
                      "ListOfLE", "LE", "LERef",
                      "SetOfSE", "SE", "SERef",
                      "MapOfKToV", "K", "KRef", "V", "VRef")),

            c("exclude: hierarchy",
                    l(Alpha.class),
                    f -> f.exclude("Beta"),
                    l("Alpha", "Charlie", "Long")),
            c("exclude: collections #1",
                    l(Omega.class),
                    f -> f.exclude("Omega"),
                    l("ListOfLE", "LE", "LERef",
                            "SetOfSE", "SE", "SERef",
                            "MapOfKToV", "K", "KRef", "V", "VRef")),
            c("exclude: collections #2",
                    l(Omega.class),
                    f -> f.exclude("ListOfLE")
                          .exclude("SetOfSE")
                          .exclude("MapOfKToV"),
                    l("Omega",
                      "LE", "LERef",
                      "SE", "SERef",
                      "K", "KRef", "V", "VRef")),

            c("recursive exclude: hierarchy",
                    l(Alpha.class),
                    f -> f.excludeRecursive("Beta"),
                    l("Alpha")),
            c("recursive exclude: collections #1",
                    l(Omega.class),
                    f -> f.excludeRecursive("Omega"),
                    none()),
            c("recursive exclude: collections #2",
                    l(Omega.class),
                    f -> f.excludeRecursive("ListOfLE")
                          .excludeRecursive("SetOfSE")
                          .excludeRecursive("MapOfKToV"),
                    l("Omega")),

            c("re-include: hierarchy",
                    l(Alpha.class),
                    f -> f.excludeRecursive("Beta")
                          .include("Long"),
                    l("Alpha", "Long")),
            c("re-include: collections",
                    l(Omega.class),
                    f -> f.excludeRecursive("Omega")
                          .include("ListOfLE")
                          .include("SetOfSE")
                          .include("MapOfKToV"),
                    l("ListOfLE", "SetOfSE", "MapOfKToV")),

            c("recursive re-include: collections",
                    l(Omega.class),
                    f -> f.excludeRecursive("Omega")
                          .includeRecursive("ListOfLE")
                          .includeRecursive("SetOfSE")
                          .includeRecursive("MapOfKToV"),
                    l("ListOfLE", "LE", "LERef",
                      "SetOfSE", "SE", "SERef",
                      "MapOfKToV", "K", "KRef", "V", "VRef"))
    );
    private static final Collection<Object[]> typeAndFieldCases = asList(
            /*
             * Field-specific inclusions/exclusions not fully supported.
             *
             * For now:
             *
             * • type inclusions always include all fields
             * • type exclusions always exclude all fields
             * • field-specific inclusions are resolved equivalent to a type inclusion
             * • field-specific exclusions do nothing
             */
            c("type include",
                    l(Charlie.class),
                    f -> f.excludeAll()
                          .include("Charlie"),
                    l("Charlie", "Charlie.i", "Charlie.l", "Charlie.s")),
            c("type exclude",
                    l(Beta.class),
                    f -> f.exclude("Beta")
                          .exclude("Long"),
                    l("Charlie", "Charlie.i", "Charlie.l", "Charlie.s")),

            c("include inline fields",
                    l(Charlie.class),
                    f -> f.excludeAll()
                          .include("Charlie", "l"),
                    l("Charlie", "Charlie.l")),
            c("include scalar ref field",
                    l(Beta.class),
                    f -> f.excludeAll()
                          .include("Beta", "l"),
                    l("Beta", "Beta.l")),
            c("include object ref field",
                    l(Beta.class),
                    f -> f.excludeAll()
                          .include("Beta", "charlie"),
                    l("Beta", "Beta.charlie")),
            c("recursive include scalar ref field",
                    l(Beta.class),
                    f -> f.excludeAll()
                          .includeRecursive("Beta", "l"),
                    l("Beta", "Beta.l", "Long", "Long.value")),
            c("recursive include object ref field",
                    l(Beta.class),
                    f -> f.excludeAll()
                          .includeRecursive("Beta", "charlie"),
                    l("Beta", "Beta.charlie", "Charlie", "Charlie.i", "Charlie.l", "Charlie.s"))
    );

    @RunWith(Parameterized.class)
    public static class TypeOnly extends AbstractTypeFilterTest {
        private final List<Class<?>> models;
        private final UnaryOperator<TypeFilter.Builder> filteredBy;
        private final List<String> expected;

        @Parameterized.Parameters(name = "{index}: {0}{1} ➡︎ {4}")
        public static Collection<Object[]> cases() {
            return typeCases;
        }

        public TypeOnly(String skip, String msg, List<Class<?>> models,
                        UnaryOperator<TypeFilter.Builder> filteredBy,
                        List<String> expected) {
            super(skip);
            this.models = models;
            this.filteredBy = filteredBy;
            this.expected = expected;
        }

        @Test
        public void verify() {
            if (skip) return;

            List<HollowSchema> schemas = generateSchema(models);

            TypeFilter subject = filteredBy
                    .apply(newTypeFilter())
                    .resolve(schemas);

            Set<String> included = schemas.stream()
                    .map(HollowSchema::getName)
                    .filter(name -> subject.includes(name))
                    .collect(toSet());
            if (expected.isEmpty()) {
                assertThat(included).isEmpty();
            } else {
                assertThat(included).containsOnly(expected.toArray(new String[0]));
            }
        }
    }

    @RunWith(Parameterized.class)
    public static class TypeAndField extends AbstractTypeFilterTest {
        private final List<Class<?>> models;
        private final UnaryOperator<TypeFilter.Builder> filteredBy;
        private final List<String> expected;

        @Parameterized.Parameters(name = "{index}: {0}{1} ➡︎ {4}")
        public static Collection<Object[]> cases() {
            return typeAndFieldCases;
        }

        public TypeAndField(String skip, String msg, List<Class<?>> models,
                            UnaryOperator<TypeFilter.Builder> filteredBy,
                            List<String> expected) {
            super(skip);
            this.models = models;
            this.filteredBy = filteredBy;
            this.expected = expected;
        }

        @Test
        public void verify() {
            if (skip) return;
            List<HollowSchema> schemas = generateSchema(models);

            TypeFilter subject = filteredBy
                    .apply(newTypeFilter())
                    .resolve(schemas);

            Set<String> included = schemas
                    .stream()
                    .flatMap(schema -> {
                        String typeName = schema.getName();
                        Stream<String> typeStream = subject.includes(typeName) ? Stream.of(typeName) : Stream.empty();
                        if (schema.getSchemaType() == OBJECT) {
                            HollowObjectSchema os = (HollowObjectSchema) schema;
                            Set<String> fields = IntStream.range(0, os.numFields())
                                                          .mapToObj(os::getFieldName)
                                                          .filter(f -> subject.includes(typeName, f))
                                                          .map(f -> typeName + "." + f)
                                                          .collect(toSet());
                            return fields.isEmpty() ? Stream.empty() : Stream.concat(typeStream, fields.stream());
                        } else {
                            return typeStream;
                        }
                    })
                    .collect(toSet());

            if (this.expected.isEmpty()) {
                assertThat(included).isEmpty();
            } else {
                assertThat(included).containsOnly(this.expected.toArray(new String[0]));
            }
        }
    }

    private static abstract class AbstractTypeFilterTest extends TypeFilterTest {
        static final String SKIP = "SKIP:";
        protected final boolean skip;

        protected AbstractTypeFilterTest(String skip) {
            this.skip = skip == SKIP;
        }
    }

    private static Object[] c(String msg, List<Class<?>> models, UnaryOperator<TypeFilter.Builder> filteredBy, List<String> expected) {
        return new Object[] { "", msg, models, filteredBy, expected };
    }

    private static Object[] skip(String msg, List<Class<?>> models, UnaryOperator<TypeFilter.Builder> filteredBy, List<String> expected) {
        return new Object[] { AbstractTypeFilterTest.SKIP, msg, models, filteredBy, expected };
    }

    private static <T> List<T> none() {
        return emptyList();
    }

    private static <T> List<T> l(T...items) {
        assert items.length > 0;
        return Arrays.asList(items);
    }

    /**
     * A type with zero records is omitted from the read state, so a schema that a recursively-included
     * type references can be absent from the schema set. The resolver must skip the missing subtree
     * rather than dereference a null schema (which NPEs at {@link HollowSchema#getName()} in
     * {@code Resolver.descendants}). The following tests cover every path that follows a reference
     * during resolution: object reference fields (type- and field-recursive, include and exclude),
     * collection elements, and map keys/values.
     */
    @Test
    public void resolveToleratesMissingReferencedObjectType() {
        // Alpha -> Beta -> Charlie; Charlie has no records, so its schema is absent.
        List<HollowSchema> schemas = schemasWithout(l(Alpha.class), "Charlie");

        TypeFilter subject = newTypeFilter()
                .excludeAll()
                .includeRecursive("Alpha")
                .resolve(schemas);

        assertThat(subject.includes("Alpha")).isTrue();
        assertThat(subject.includes("Beta")).isTrue();
        // The reference field to the missing type is still included -- identical to an unfiltered read,
        // where wireTypeStatesToSchemas wires the field's target type state to null.
        assertThat(subject.includes("Beta", "charlie")).isTrue();
        // ...but the missing type itself is not included.
        assertThat(subject.includes("Charlie")).isFalse();
    }

    @Test
    public void resolveHaltsRecursionAtMissingIntermediateType() {
        // Alpha -> Beta -> {Charlie, Long}; the intermediate Beta is absent. Types reachable ONLY
        // through the missing Beta must not be pulled in (they would be empty in the read state too).
        List<HollowSchema> schemas = schemasWithout(l(Alpha.class), "Beta");

        TypeFilter subject = newTypeFilter()
                .excludeAll()
                .includeRecursive("Alpha")
                .resolve(schemas);

        assertThat(subject.includes("Alpha")).isTrue();
        assertThat(subject.includes("Alpha", "beta")).isTrue();
        assertThat(subject.includes("Charlie")).isFalse();
        assertThat(subject.includes("Long")).isFalse();
    }

    @Test
    public void resolveToleratesMissingReferencedTypeForFieldRecursiveInclude() {
        // Field-recursive include drives the recursion from the field action (fa.recursive) rather
        // than an inherited type action -- a distinct entry into the guarded reference lookup.
        List<HollowSchema> schemas = schemasWithout(l(Beta.class), "Charlie");

        TypeFilter subject = newTypeFilter()
                .excludeAll()
                .includeRecursive("Beta", "charlie")
                .resolve(schemas);

        assertThat(subject.includes("Beta")).isTrue();
        assertThat(subject.includes("Beta", "charlie")).isTrue();
        assertThat(subject.includes("Charlie")).isFalse();
    }

    @Test
    public void resolveToleratesMissingReferencedTypeUnderExcludeRecursive() {
        // The guard must also hold when the walk is driven by an exclude (not include) action.
        // Echo is disjoint from Alpha's hierarchy and must remain included by the include-all base.
        List<HollowSchema> schemas = schemasWithout(l(Alpha.class, Echo.class), "Charlie");

        TypeFilter subject = newTypeFilter()
                .excludeRecursive("Alpha")
                .resolve(schemas);

        assertThat(subject.includes("Echo")).isTrue();
        assertThat(subject.includes("Alpha")).isFalse();
        assertThat(subject.includes("Beta")).isFalse();
    }

    @Test
    public void resolveToleratesMissingCollectionElementType() {
        // Omega -> ListOfLE (element LE); LE is absent. LERef is reachable only through LE.
        List<HollowSchema> schemas = schemasWithout(l(Omega.class), "LE");

        TypeFilter subject = newTypeFilter()
                .excludeAll()
                .includeRecursive("Omega")
                .resolve(schemas);

        assertThat(subject.includes("Omega")).isTrue();
        assertThat(subject.includes("ListOfLE")).isTrue();
        assertThat(subject.includes("LERef")).isFalse();
    }

    @Test
    public void resolveToleratesMissingMapValueTypeAndKeepsKeySubtree() {
        // Omega -> MapOfKToV; value type V is absent. The key subtree (K, KRef) must survive because
        // the two map guards are independent.
        List<HollowSchema> schemas = schemasWithout(l(Omega.class), "V");

        TypeFilter subject = newTypeFilter()
                .excludeAll()
                .includeRecursive("Omega")
                .resolve(schemas);

        assertThat(subject.includes("MapOfKToV")).isTrue();
        assertThat(subject.includes("K")).isTrue();
        assertThat(subject.includes("KRef")).isTrue();
        assertThat(subject.includes("VRef")).isFalse(); // reachable only through the missing V
    }

    @Test
    public void resolveToleratesMissingMapKeyTypeAndKeepsValueSubtree() {
        // Mirror of the previous test: key type K is absent; the value subtree (V, VRef) must survive.
        List<HollowSchema> schemas = schemasWithout(l(Omega.class), "K");

        TypeFilter subject = newTypeFilter()
                .excludeAll()
                .includeRecursive("Omega")
                .resolve(schemas);

        assertThat(subject.includes("MapOfKToV")).isTrue();
        assertThat(subject.includes("V")).isTrue();
        assertThat(subject.includes("VRef")).isTrue();
        assertThat(subject.includes("KRef")).isFalse(); // reachable only through the missing K
    }

    @Test
    public void resolveToleratesMissingMapKeyAndValueTypes() {
        // Both key and value absent -> exercises both guarded ternary branches at once.
        List<HollowSchema> schemas = schemasWithout(l(Omega.class), "K", "V");

        TypeFilter subject = newTypeFilter()
                .excludeAll()
                .includeRecursive("Omega")
                .resolve(schemas);

        assertThat(subject.includes("Omega")).isTrue();
        assertThat(subject.includes("MapOfKToV")).isTrue();
        assertThat(subject.includes("KRef")).isFalse();
        assertThat(subject.includes("VRef")).isFalse();
    }

    private static List<HollowSchema> schemasWithout(List<Class<?>> models, String... missingTypes) {
        Set<String> missing = new HashSet<>(asList(missingTypes));
        List<HollowSchema> schemas = new ArrayList<>(generateSchema(models));
        schemas.removeIf(s -> missing.contains(s.getName()));
        return schemas;
    }

    private static List<HollowSchema> generateSchema(List<Class<?>> models) {
        HollowWriteStateEngine wse = new HollowWriteStateEngine();
        HollowObjectMapper om = new HollowObjectMapper(wse);

        for (Class<?> model : models) {
            om.initializeTypeState(model);
        }

        return wse.getSchemas();
    }
}

@SuppressWarnings("unused")
class Alpha {
    int i;
    Beta beta;
}

@SuppressWarnings("unused")
class Beta {
    @HollowInline
    String s;
    Long l;
    Charlie charlie;
}

@SuppressWarnings("unused")
class Charlie {
    int i;
    long l;
    @HollowInline
    String s;
}

@SuppressWarnings("unused")
class Echo {
    long l;
}

@SuppressWarnings("unused")
class Omega {
    List<LE> list;
    Set<SE> set;
    Map<K,V> map;

    static class LE {
        LERef ref;
    }
    static class LERef {
        int i;
    }
    static class SE {
        SERef ref;
    }
    static class SERef {
        int i;
    }
    static class K {
        KRef ref;
    }
    static class KRef {
        int i;
    }
    static class V {
        VRef ref;
    }
    static class VRef {
        int i;
    }
}
