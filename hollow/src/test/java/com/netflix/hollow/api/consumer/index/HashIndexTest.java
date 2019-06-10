package com.netflix.hollow.api.consumer.index;

import static java.util.stream.Collectors.toList;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.InMemoryBlobStore;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

public class HashIndexTest {
    // Map of primitive class to box class
    private static final Map<Class<?>, Class<?>> primitiveClasses;
    static {
        primitiveClasses = new HashMap<>();
        primitiveClasses.put(boolean.class, Boolean.class);
        primitiveClasses.put(byte.class, Byte.class);
        primitiveClasses.put(short.class, Short.class);
        primitiveClasses.put(char.class, Character.class);
        primitiveClasses.put(int.class, Integer.class);
        primitiveClasses.put(long.class, Long.class);
        primitiveClasses.put(float.class, Float.class);
        primitiveClasses.put(double.class, Double.class);
    }

    static HollowConsumer consumer;

    private static DataModel.Consumer.Api api;

    @BeforeClass
    public static void setup() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();

        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = producer.runCycle(ws -> {
            ws.add(new DataModel.Producer.References());

            for (int i = 0; i < 100; i++) {
                ws.add(new DataModel.Producer.TypeA(1, "TypeA" + i));
                ws.add(new DataModel.Producer.TypeB(1, "TypeB" + i));
            }
            ws.add(new DataModel.Producer.TypeWithTypeB("bar", new DataModel.Producer.TypeB(7, "c")));
            ws.add(new DataModel.Producer.TypeWithTypeB("foo", new DataModel.Producer.TypeB(8, "a")));
            ws.add(new DataModel.Producer.TypeWithTypeB("foo", new DataModel.Producer.TypeB(7, "b")));
        });

        consumer = HollowConsumer.withBlobRetriever(blobStore)
                .withGeneratedAPIClass(DataModel.Consumer.Api.class)
                .build();
        consumer.triggerRefreshTo(v1);

        api = consumer.getAPI(DataModel.Consumer.Api.class);
    }

    public static abstract class MatchTestParameterized<T extends HollowObject, Q> extends HashIndexTest {
        final String path;
        final Class<Q> type;
        final Q value;
        final Class<T> selectType;

        MatchTestParameterized(String path, Class<Q> type, Q value, Class<T> selectType) {
            this.path = path;
            this.type = type;
            this.value = value;
            this.selectType = selectType;
        }

        @Test
        public void test() {
            HashIndex<T, Q> hi = HashIndex
                    .from(consumer, selectType)
                    .usingPath(path, type);

            List<T> r = hi
                    .findMatches(value)
                    .collect(toList());

            Assert.assertEquals(1, r.size());
            Assert.assertTrue(selectType.isInstance(r.get(0)));
            Assert.assertEquals(0, r.get(0).getOrdinal());
        }
    }

    // path, type, value
    private static List<Object[]> valuesDataProvider() {
        DataModel.Producer.Values values = new DataModel.Producer.Values();
        return Stream.of(DataModel.Producer.Values.class.getDeclaredFields())
                .flatMap(f -> {
                    String path = f.getName();
                    Class<?> type = f.getType();
                    Object value;
                    try {
                        value = f.get(values);
                    } catch (IllegalAccessException e) {
                        throw new InternalError();
                    }

                    Object[] args = new Object[] {path, type, value};
                    if (type.isPrimitive()) {
                        return Stream.of(args,
                                new Object[] {path, primitiveClasses.get(type), value}
                        );
                    } else {
                        return Stream.<Object[]>of(args);
                    }
                })
                .collect(toList());
    }

    @RunWith(Parameterized.class)
    public static class MatchOnValuesTest<Q> extends MatchTestParameterized<DataModel.Consumer.Values, Q> {
        // path[type] = value
        @Parameters(name = "{index}: {0}[{1}] = {2}")
        public static Collection<Object[]> data() {
            return valuesDataProvider();
        }

        public MatchOnValuesTest(String path, Class<Q> type, Q value) {
            super(path, type, value, DataModel.Consumer.Values.class);
        }
    }


    @RunWith(Parameterized.class)
    public static class MatchOnValuesIllegalTypeTest extends HashIndexTest {
        // path[type] = value
        @Parameters(name = "{index}: {0}[{1}] = {2}")
        public static Collection<Object[]> data() {
            return valuesDataProvider();
        }

        final String path;
        final Class<?> type;
        final Object value;

        public MatchOnValuesIllegalTypeTest(String path, Class<?> type, Object value) {
            this.path = path;
            this.type = type;
            this.value = value;
        }

        @Test(expected = IllegalArgumentException.class)
        public void test() {
            HashIndex
                    .from(consumer, DataModel.Consumer.Values.class)
                    .usingPath(path, Object.class);
        }
    }

    public static class MatchOnValuesBeanTest extends HashIndexTest {
        static class ValueFieldsQuery {
            @FieldPath
            boolean _boolean;
            @FieldPath
            byte _byte;
            @FieldPath
            short _short;
            @FieldPath
            char _char;
            @FieldPath
            int _int;
            @FieldPath
            long _long;
            @FieldPath
            float _float;
            @FieldPath
            double _double;
            @FieldPath
            byte[] _bytes;
            @FieldPath
            char[] _chars;

            ValueFieldsQuery(DataModel.Producer.Values v) {
                this._boolean = v._boolean;
                this._byte = v._byte;
                this._short = v._short;
                this._char = v._char;
                this._int = v._int;
                this._long = v._long;
                this._float = v._float;
                this._double = v._double;
                this._bytes = v._bytes.clone();
                this._chars = v._chars.clone();
            }

            static ValueFieldsQuery create() {
                return new ValueFieldsQuery(new DataModel.Producer.Values());
            }
        }

        @Test
        public void testFields() {
            HashIndex<DataModel.Consumer.Values, ValueFieldsQuery> hi = HashIndex
                    .from(consumer, DataModel.Consumer.Values.class)
                    .usingBean(ValueFieldsQuery.class);

            List<DataModel.Consumer.Values> r = hi.findMatches(ValueFieldsQuery.create())
                    .collect(toList());

            Assert.assertEquals(1, r.size());
            Assert.assertEquals(0, r.get(0).getOrdinal());
        }

        @SuppressWarnings("unused")
        static class ValueMethodsQuery {
            boolean _boolean;
            byte _byte;
            short _short;
            char _char;
            int _int;
            long _long;
            float _float;
            double _double;
            byte[] _bytes;
            char[] _chars;

            @FieldPath("_boolean")
            boolean is_boolean() {
                return _boolean;
            }

            @FieldPath("_byte")
            byte get_byte() {
                return _byte;
            }

            @FieldPath("_short")
            short get_short() {
                return _short;
            }

            @FieldPath("_char")
            char get_char() {
                return _char;
            }

            @FieldPath("_int")
            int get_int() {
                return _int;
            }

            @FieldPath("_long")
            long get_long() {
                return _long;
            }

            @FieldPath("_float")
            float get_float() {
                return _float;
            }

            @FieldPath("_double")
            double get_double() {
                return _double;
            }

            @FieldPath("_bytes")
            byte[] get_bytes() {
                return _bytes;
            }

            @FieldPath("_chars")
            char[] get_chars() {
                return _chars;
            }

            ValueMethodsQuery(DataModel.Producer.Values v) {
                this._boolean = v._boolean;
                this._byte = v._byte;
                this._short = v._short;
                this._char = v._char;
                this._int = v._int;
                this._long = v._long;
                this._float = v._float;
                this._double = v._double;
                this._bytes = v._bytes.clone();
                this._chars = v._chars.clone();
            }

            static ValueMethodsQuery create() {
                return new ValueMethodsQuery(new DataModel.Producer.Values());
            }
        }

        @Test
        public void testMethods() {
            HashIndex<DataModel.Consumer.Values, ValueMethodsQuery> hi = HashIndex
                    .from(consumer, DataModel.Consumer.Values.class)
                    .usingBean(ValueMethodsQuery.class);

            List<DataModel.Consumer.Values> r = hi.findMatches(ValueMethodsQuery.create())
                    .collect(toList());

            Assert.assertEquals(1, r.size());
            Assert.assertEquals(0, r.get(0).getOrdinal());
        }
    }

    // path, type, value
    private static List<Object[]> boxesDataProvider() {
        DataModel.Producer.Boxes values = new DataModel.Producer.Boxes();
        return Stream.of(DataModel.Producer.Boxes.class.getDeclaredFields())
                .map(f -> {
                    String path = f.getName() + ".value";
                    Class<?> type = f.getType();
                    Object value;
                    try {
                        value = f.get(values);
                    } catch (IllegalAccessException e) {
                        throw new InternalError();
                    }

                    return new Object[] {path, type, value};
                })
                .collect(toList());
    }

    @RunWith(Parameterized.class)
    public static class MatchOnBoxesValuesTest<Q> extends MatchTestParameterized<DataModel.Consumer.Boxes, Q> {
        // path[type] = value
        @Parameters(name = "{index}: {0}[{1}] = {2}")
        public static Collection<Object[]> data() {
            return boxesDataProvider();
        }

        public MatchOnBoxesValuesTest(String path, Class<Q> type, Q value) {
            super(path, type, value, DataModel.Consumer.Boxes.class);
        }
    }


    private static List<Object[]> inlineBoxesDataProvider() {
        DataModel.Producer.InlineBoxes values = new DataModel.Producer.InlineBoxes();
        return Stream.of(DataModel.Producer.InlineBoxes.class.getDeclaredFields())
                .map(f -> {
                    String path = f.getName();
                    Class<?> type = f.getType();
                    Object value;
                    try {
                        value = f.get(values);
                    } catch (IllegalAccessException e) {
                        throw new InternalError();
                    }

                    return new Object[] {path, type, value};
                })
                .collect(toList());
    }

    @RunWith(Parameterized.class)
    public static class MatchOnInlineBoxesTest<Q> extends MatchTestParameterized<DataModel.Consumer.InlineBoxes, Q> {
        // path[type] = value
        @Parameters(name = "{index}: {0}[{1}] = {2}")
        public static Collection<Object[]> data() {
            return inlineBoxesDataProvider();
        }

        public MatchOnInlineBoxesTest(String path, Class<Q> type, Q value) {
            super(path, type, value, DataModel.Consumer.InlineBoxes.class);
        }
    }

    @RunWith(Parameterized.class)
    public static class MatchOnMappedReferencesTest<Q>
            extends MatchTestParameterized<DataModel.Consumer.MappedReferencesToValues, Q> {
        // path[type] = value
        @Parameters(name = "{index}: {0}[{1}] = {2}")
        public static Collection<Object[]> data() {
            return Arrays.asList(
                    new Object[] {"date.value", long.class, 0L},
                    new Object[] {"number._name", String.class, "ONE"}
            );
        }

        public MatchOnMappedReferencesTest(String path, Class<Q> type, Q value) {
            super(path, type, value, DataModel.Consumer.MappedReferencesToValues.class);
        }
    }

    @RunWith(Parameterized.class)
    public static class MatchOnReferencesTest<Q extends HollowRecord> extends HashIndexTest {
        @Parameters(name = "{index}: {0}[{1} = {2}]")
        public static Collection<Object[]> data() {
            return Arrays.asList(
                    args("values", DataModel.Consumer.Values.class,
                            () -> api.getValues(0)),
                    args("boxes._string", DataModel.Consumer.HString.class,
                            () -> api.getHString(0)),
                    args("sequences.list", DataModel.Consumer.ListOfBoxes.class,
                            () -> api.getListOfBoxes(0)),
                    args("sequences.map.key._string", DataModel.Consumer.HString.class,
                            () -> api.getHString(0)),
                    args("referenceWithStrings", DataModel.Consumer.ReferenceWithStringsRenamed.class,
                            () -> api.getReferenceWithStringsRenamed(0)),
                    args("referenceWithStrings._string1", DataModel.Consumer.HString.class,
                            () -> api.getHString(0)),
                    args("referenceWithStrings._string2", DataModel.Consumer.FieldOfStringRenamed.class,
                            () -> api.getFieldOfStringRenamed(0))
            );
        }

        static <Q extends HollowRecord> Object[] args(String path, Class<Q> type, Supplier<Q> s) {
            return new Object[] {path, type, s};
        }

        final String path;
        final Class<Q> type;
        final Q value;

        public MatchOnReferencesTest(String path, Class<Q> type, Supplier<Q> value) {
            this.path = path;
            this.type = type;
            this.value = value.get();
        }

        @Test
        public void test() {
            HashIndex<DataModel.Consumer.References, Q> hi = HashIndex
                    .from(consumer, DataModel.Consumer.References.class)
                    .usingPath(path, type);

            List<DataModel.Consumer.References> r = hi.findMatches(value)
                    .collect(toList());

            Assert.assertEquals(1, r.size());
            Assert.assertEquals(0, r.get(0).getOrdinal());
        }
    }


    @RunWith(Parameterized.class)
    public static class SelectOnValuesTest extends HashIndexTest {
        // path[type] = value
        @Parameters(name = "{index}: {0}[{1}] = {2}")
        public static Collection<Object[]> data() {
            return valuesDataProvider();
        }

        final String path;
        final Class<?> type;
        final Object value;

        public SelectOnValuesTest(String path, Class<?> type, Object value) {
            this.path = path;
            this.type = type;
            this.value = value;
        }

        @Test(expected = IllegalArgumentException.class)
        public void test() {
            HashIndex
                    .from(consumer, DataModel.Consumer.Values.class)
                    .selectField(path, GenericHollowObject.class)
                    .usingPath("values._int", int.class);
        }
    }

    @RunWith(Parameterized.class)
    public static class SelectTest<S extends HollowRecord> extends HashIndexTest {
        @Parameters(name = "{index}: {0}[{1}]")
        public static Collection<Object[]> data() {
            return Arrays.asList(
                    args("boxes._string", DataModel.Consumer.HString.class),

                    args("boxes._string", GenericHollowObject.class),

                    args("sequences.list", DataModel.Consumer.ListOfBoxes.class),
                    args("sequences.list.element", DataModel.Consumer.Boxes.class),
                    args("sequences.list.element._string", DataModel.Consumer.HString.class),

                    args("sequences.set", DataModel.Consumer.SetOfBoxes.class),
                    args("sequences.set.element", DataModel.Consumer.Boxes.class),
                    args("sequences.set.element._string", DataModel.Consumer.HString.class),

                    args("sequences.map", DataModel.Consumer.MapOfBoxesToBoxes.class),
                    args("sequences.map.key", DataModel.Consumer.Boxes.class),
                    args("sequences.map.key._string", DataModel.Consumer.HString.class),
                    args("sequences.map.value", DataModel.Consumer.Boxes.class),
                    args("sequences.map.value._string", DataModel.Consumer.HString.class),

                    args("referenceWithStrings", DataModel.Consumer.ReferenceWithStringsRenamed.class),
                    args("referenceWithStrings._string1", DataModel.Consumer.HString.class),
                    args("referenceWithStrings._string2", DataModel.Consumer.FieldOfStringRenamed.class)
            );
        }

        static <S extends HollowRecord> Object[] args(String path, Class<S> type) {
            return new Object[] {path, type};
        }

        final String path;
        final Class<S> type;

        public SelectTest(String path, Class<S> type) {
            this.path = path;
            this.type = type;
        }

        @Test
        public void test() {
            HashIndexSelect<DataModel.Consumer.References, S, Integer> hi = HashIndex
                    .from(consumer, DataModel.Consumer.References.class)
                    .selectField(path, type)
                    .usingPath("values._int", int.class);

            List<S> r = hi.findMatches(1)
                    .collect(toList());

            Assert.assertEquals(1, r.size());
            Assert.assertTrue(type.isInstance(r.get(0)));
            Assert.assertEquals(0, r.get(0).getOrdinal());
        }
    }


    public static class TestManyMatches extends HashIndexTest {
        @Test
        public void test() {
            HashIndex<DataModel.Consumer.TypeA, Integer> hi = HashIndex
                    .from(consumer, DataModel.Consumer.TypeA.class)
                    .usingPath("i", int.class);

            List<DataModel.Consumer.TypeA> r = hi.findMatches(1)
                    .sorted(Comparator.comparingInt(HollowObject::getOrdinal)).collect(toList());
            Assert.assertEquals(100, r.size());
            for (int i = 0; i < r.size(); i++) {
                Assert.assertEquals(i, r.get(i).getOrdinal());
            }
        }

        @Test
        public void testTypeAWithSelect() {
            HashIndexSelect<DataModel.Consumer.TypeA, DataModel.Consumer.HString, Integer> hi = HashIndex
                    .from(consumer, DataModel.Consumer.TypeA.class)
                    .selectField("s", DataModel.Consumer.HString.class)
                    .usingPath("i", int.class);

            List<String> r = hi.findMatches(1)
                    .sorted(Comparator.comparingInt(HollowObject::getOrdinal))
                    .map(DataModel.Consumer.HString::getValue)
                    .collect(toList());
            Assert.assertEquals(100, r.size());
            for (int i = 0; i < r.size(); i++) {
                Assert.assertEquals("TypeA" + i, r.get(i));
            }
        }

        @Test
        public void testTypeBWithSelect() {
            HashIndexSelect<DataModel.Consumer.TypeBSuffixed, DataModel.Consumer.HString, Integer> hi = HashIndex
                    .from(consumer, DataModel.Consumer.TypeBSuffixed.class)
                    .selectField("s", DataModel.Consumer.HString.class)
                    .usingPath("i", int.class);

            List<String> r = hi.findMatches(1)
                    .sorted(Comparator.comparingInt(HollowObject::getOrdinal))
                    .map(DataModel.Consumer.HString::getValue)
                    .collect(toList());
            Assert.assertEquals(100, r.size());
            for (int i = 0; i < r.size(); i++) {
                Assert.assertEquals("TypeB" + i, r.get(i));
            }
        }

        @Test
        public void testWithSelectRootTypeGenericHollowObject() {
            HashIndexSelect<DataModel.Consumer.TypeA, GenericHollowObject, Integer> hi = HashIndex
                    .from(consumer, DataModel.Consumer.TypeA.class)
                    .selectField("", GenericHollowObject.class)
                    .usingPath("i", int.class);

            boolean r = hi.findMatches(1)
                    .sorted(Comparator.comparingInt(HollowObject::getOrdinal))
                    .mapToInt(gho -> gho.getInt("i"))
                    .allMatch(i -> i == 1);
            Assert.assertTrue(r);
        }

        @Test
        public void testWithSelectGenericHollowObject() {
            HashIndexSelect<DataModel.Consumer.TypeA, GenericHollowObject, Integer> hi = HashIndex
                    .from(consumer, DataModel.Consumer.TypeA.class)
                    .selectField("s", GenericHollowObject.class)
                    .usingPath("i", int.class);

            List<String> r = hi.findMatches(1)
                    .sorted(Comparator.comparingInt(HollowObject::getOrdinal))
                    .map(gho -> gho.getString("value"))
                    .collect(toList());
            Assert.assertEquals(100, r.size());
            for (int i = 0; i < r.size(); i++) {
                Assert.assertEquals("TypeA" + i, r.get(i));
            }
        }
    }


    public static class ErrorsTest extends HashIndexTest {
        static class Unknown extends HollowObject {
            Unknown(HollowObjectDelegate delegate, int ordinal) {
                super(delegate, ordinal);
            }
        }

        @Test(expected = IllegalArgumentException.class)
        public void testUnknownRootSelectType() {
            HashIndex
                    .from(consumer, Unknown.class)
                    .usingPath("values", DataModel.Consumer.Values.class);
        }

        @Test(expected = IllegalArgumentException.class)
        public void testUnknownSelectType() {
            HashIndex
                    .from(consumer, DataModel.Consumer.References.class)
                    .selectField("values", Unknown.class)
                    .usingPath("values", DataModel.Consumer.Values.class);
        }

        @Test(expected = IllegalArgumentException.class)
        public void testEmptyMatchPath() {
            HashIndex
                    .from(consumer, DataModel.Consumer.References.class)
                    .usingPath("", DataModel.Consumer.References.class);
        }
    }

    public static class GeneratedSuffixTest extends HashIndexTest {
        @Test
        public void testMatch() {
            HashIndex<DataModel.Consumer.TypeBSuffixed, Integer> hi = HashIndex
                    .from(consumer, DataModel.Consumer.TypeBSuffixed.class)
                    .usingPath("i", int.class);

            List<DataModel.Consumer.TypeBSuffixed> r = hi.findMatches(1)
                    .sorted(Comparator.comparingInt(HollowObject::getOrdinal)).collect(toList());
            Assert.assertEquals(100, r.size());
            for (int i = 0; i < r.size(); i++) {
                Assert.assertEquals(i, r.get(i).getOrdinal());
            }
        }

        @Test
        public void testSelect() {
            HashIndexSelect<DataModel.Consumer.TypeWithTypeB, DataModel.Consumer.TypeBSuffixed, String> his =
                    HashIndex.from(consumer, DataModel.Consumer.TypeWithTypeB.class)
                            .selectField("typeB", DataModel.Consumer.TypeBSuffixed.class)
                            .usingPath("foo.value", String.class);
            List<Integer> r = his.findMatches("foo")
                    .map(HollowObject::getOrdinal).sorted().collect(Collectors.toList());
            Assert.assertEquals(Arrays.asList(101, 102), r);
        }

        @Test
        public void testSelectRootTypeSuffixed() {
            HashIndexSelect<DataModel.Consumer.TypeBSuffixed, DataModel.Consumer.HString, Integer> his =
                    HashIndex.from(consumer, DataModel.Consumer.TypeBSuffixed.class)
                            .selectField("s", DataModel.Consumer.HString.class)
                            .usingPath("i", int.class);
            List<Integer> r = his.findMatches(7)
                    .map(HollowObject::getOrdinal).sorted().collect(Collectors.toList());
            Assert.assertEquals(Arrays.asList(203, 206), r);
        }
    }
}
