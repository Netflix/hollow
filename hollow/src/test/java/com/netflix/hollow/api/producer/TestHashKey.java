package com.netflix.hollow.api.producer;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.netflix.hollow.api.consumer.InMemoryBlobStore;
import com.netflix.hollow.api.objects.HollowMap;
import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.api.objects.delegate.HollowMapLookupDelegate;
import com.netflix.hollow.api.objects.delegate.HollowSetLookupDelegate;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchemaParser;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowHashKey;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.tools.filter.FilteredHollowBlobWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestHashKey {
    private InMemoryBlobStore blobStore;

    @Before
    public void setup() {
        blobStore = new InMemoryBlobStore();
    }

    /**
     * Tests that a set or map is declared with a hash key and new schema can be created
     * without a hash key
     */
    @Test
    public void testSchemaWithAndWithoutHashKeys() throws Exception {
        HollowProducer p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = p.runCycle(ws -> {
            ws.add(new Top("One", "Two"));
        });

        HollowWriteStateEngine w = p.getWriteEngine();
        Assert.assertTrue(w.getSchema("SetOfBoxString")
                .toString().contains("@HashKey"));
        Assert.assertTrue(w.getSchema("MapOfBoxStringToBoxString")
                .toString().contains("@HashKey"));


        Assert.assertFalse(((HollowSetSchema)w.getSchema("SetOfBoxString")).withoutHashKey()
                .toString().contains("@HashKey"));
        Assert.assertFalse(((HollowMapSchema)w.getSchema("MapOfBoxStringToBoxString")).withoutHashKey()
                .toString().contains("@HashKey"));

        Assert.assertFalse(HollowSchema.withoutKeys(w.getSchema("SetOfBoxString"))
                .toString().contains("@HashKey"));
        Assert.assertFalse(HollowSchema.withoutKeys(w.getSchema("MapOfBoxStringToBoxString"))
                .toString().contains("@HashKey"));
    }

    /**
     * Tests that the schema for a set or map declared with a hash key can be parsed.
     */
    @Test
    public void testSchemasParse() throws Exception {
        HollowProducer p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = p.runCycle(ws -> {
            ws.add(new Top("One", "Two"));
        });

        StringBuilder sb = new StringBuilder();
        for (HollowSchema s : p.getWriteEngine().getSchemas()) {
            sb.append(s.toString()).append("\n");
        }

        Map<String, HollowSchema> schema = HollowSchemaParser.parseCollectionOfSchemas(sb.toString()).stream().collect(
                toMap(HollowSchema::getName, s -> s));

        schema.forEach((n, s) -> {
            Assert.assertEquals(p.getWriteEngine().getSchema(n), s);
        });
    }

    /**
     * Tests that a set or map declared with a hash key with empty fields, for ordinal lookup
     * overriding the write state useDefaultHashKeys, hashes using ordinal values rather than
     * a key derived from the element type or key type.
     */
    @Test
    public void testHashOrdinals() throws Exception {
        HollowProducer p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = p.runCycle(ws -> {
            ws.add(new TopWithOrdinals(IntStream.range(0, 100).mapToObj(Integer::toString).toArray(String[]::new)));
        });

        HollowReadStateEngine r = new HollowReadStateEngine();
        HollowBlobReader br = new HollowBlobReader(r);
        br.readSnapshot(blobStore.retrieveSnapshotBlob(v1).getInputStream());

        HollowObjectTypeReadState boxString = (HollowObjectTypeReadState) r.getTypeState("BoxString");
        HollowSetTypeReadState set = (HollowSetTypeReadState) r.getTypeState("SetOfBoxString");
        HollowMapTypeReadState map = (HollowMapTypeReadState) r.getTypeState("MapOfBoxStringToBoxString");

        int size = boxString.getPopulatedOrdinals().cardinality();

        // Iterate through the ordinals using potentialMatchOrdinalIterator which should
        // find the matching ordinal but not necessarily on first iteration
        // (if there are hash collisions)

        // Test the element ordinals of the set
        for (int ordinal = 0; ordinal < size; ordinal++) {
            HollowOrdinalIterator iter = set.potentialMatchOrdinalIterator(0, ordinal);
            int potentialOrdinal;
            while ((potentialOrdinal = iter.next()) != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                if (potentialOrdinal == ordinal) {
                    break;
                }
            }
            Assert.assertEquals(ordinal, potentialOrdinal);
        }

        // Test the key ordinals of the map
        for (int ordinal = 0; ordinal < size; ordinal++) {
            HollowMapEntryOrdinalIterator iter = map.potentialMatchOrdinalIterator(0, ordinal);
            int potentialOrdinal = HollowOrdinalIterator.NO_MORE_ORDINALS;
            while (iter.next()) {
                potentialOrdinal = iter.getKey();
                if (potentialOrdinal == ordinal) {
                    break;
                }
            }
            Assert.assertEquals(ordinal, potentialOrdinal);
        }
    }

    /**
     * Tests that a snapshot filtered to remove a type can be read if the
     * type is the element type of a set or a key type of a map that declares
     * a hash key and the set or map is not removed.
     */
    @Test
    public void testHashFilter() throws Exception {
        HollowProducer p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = p.runCycle(ws -> {
            ws.add(new Top("One", "Two"));
        });

        HollowFilterConfig filterConfig = new HollowFilterConfig(true);
        filterConfig.addType("BoxString");
        FilteredHollowBlobWriter fbw = new FilteredHollowBlobWriter(filterConfig);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        fbw.filterSnapshot(blobStore.retrieveSnapshotBlob(v1).getInputStream(), baos);

        HollowReadStateEngine r = new HollowReadStateEngine();
        HollowBlobReader br = new HollowBlobReader(r);
        br.readSnapshot(new ByteArrayInputStream(baos.toByteArray()));
    }

    /**
     * Tests the transition from HollowObjectHashCodeFinder to HollowHashKey.
     * Specifically a snapshot generated using HollowObjectHashCodeFinder can be
     * used to restore state from which a new snapshot is generated using HollowHashKey.
     */
    @Test
    public void testTransitionWithRestore() throws Exception {
        HollowProducer p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();
        p.getObjectMapper().doNotUseDefaultHashKeys();
        setHashCodeFinder(p.getWriteEngine(), new CustomHashCodeFinder());

        long v1 = p.runCycle(ws -> {
            ws.add(new Top(
                    IntStream.range(0, 100).mapToObj(Integer::toString).collect(toList()),
                    IntStream.range(0, 100).boxed().collect(toList()),
                    LongStream.range(Integer.MAX_VALUE, Integer.MAX_VALUE + 100L).boxed().collect(toList())
            ));
        });

        p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();
        p.initializeDataModel(Top.class);
        p.restore(v1, blobStore);

        // Checksum should fail if hash algorithm differs between v1 and v2
        long v2 = p.runCycle(ws -> {
            ws.add(new Top(
                    IntStream.range(0, 101).mapToObj(Integer::toString).collect(toList()),
                    IntStream.range(0, 101).boxed().collect(toList()),
                    LongStream.range(Integer.MAX_VALUE, Integer.MAX_VALUE + 101L).boxed().collect(toList())
            ));
        });


        // Although checksum should pass check the ordinals

        HollowReadStateEngine r = new HollowReadStateEngine();
        HollowBlobReader br = new HollowBlobReader(r);
        br.readSnapshot(blobStore.retrieveSnapshotBlob(v2).getInputStream());

        HollowObjectTypeReadState boxString = (HollowObjectTypeReadState) r.getTypeState("BoxString");
        HollowSetTypeReadState set = (HollowSetTypeReadState) r.getTypeState("SetOfBoxString");
        HollowMapTypeReadState map = (HollowMapTypeReadState) r.getTypeState("MapOfBoxStringToBoxString");

        // Iterate through the ordinals using potentialMatchOrdinalIterator which should
        // find the matching ordinal but not necessarily on first iteration
        // (if there are hash collisions)

        Map<Integer, String> boxStringOrdinalMap = boxString.getPopulatedOrdinals().stream().boxed().collect(
                toMap(o -> o,
                        o -> boxString.readString(o, 0)));

        boxStringOrdinalMap.forEach((ordinal, string) -> {
            HollowOrdinalIterator iter = set.potentialMatchOrdinalIterator(1, string.hashCode());
            int potentialOrdinal;
            while ((potentialOrdinal = iter.next()) != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                if (potentialOrdinal == ordinal) {
                    break;
                }
            }
            Assert.assertEquals((int) ordinal, potentialOrdinal);
        });

        boxStringOrdinalMap.forEach((ordinal, string) -> {
            HollowMapEntryOrdinalIterator iter = map.potentialMatchOrdinalIterator(1, string.hashCode());
            int potentialOrdinal = -1;
            while (iter.next()) {
                potentialOrdinal = iter.getKey();
                if (potentialOrdinal == ordinal) {
                    break;
                }
            }
            Assert.assertEquals((int) ordinal, potentialOrdinal);
        });
    }

    /**
     * Tests that a produce fails with a checksum failure.
     * The HollowObjectHashCodeFinder returns bogus hash codes causing failure.
     */
    @Test(expected = HollowProducer.ChecksumValidationException.class)
    public void testTransitionWithRestoreFail() throws Exception {
        HollowProducer p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();
        p.getObjectMapper().doNotUseDefaultHashKeys();
        setHashCodeFinder(p.getWriteEngine(), new CustomHashCodeFinder() {
            @Override public int hashCode(String typeName, int ordinal, Object objectToHash) {
                // Hash differs from @HollowHashKey hash
                return 0;
            }
        });

        long v1 = p.runCycle(ws -> {
            ws.add(new Top(
                    IntStream.range(0, 100).mapToObj(Integer::toString).collect(toList()),
                    IntStream.range(0, 100).boxed().collect(toList()),
                    LongStream.range(Integer.MAX_VALUE, Integer.MAX_VALUE + 100L).boxed().collect(toList())
            ));
        });

        p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();
        p.initializeDataModel(Top.class);
        p.restore(v1, blobStore);

        // Checksum should fail if hash algorithm differs between v1 and v2
        long v2 = p.runCycle(ws -> {
            ws.add(new Top(
                    IntStream.range(0, 101).mapToObj(Integer::toString).collect(toList()),
                    IntStream.range(0, 101).boxed().collect(toList()),
                    LongStream.range(Integer.MAX_VALUE, Integer.MAX_VALUE + 101L).boxed().collect(toList())
            ));
        });
    }

    static void setHashCodeFinder(HollowWriteStateEngine e, HollowObjectHashCodeFinder f) throws Exception {
        Field hashCodeFinder = e.getClass().getDeclaredField("hashCodeFinder");
        hashCodeFinder.setAccessible(true);
        hashCodeFinder.set(e, f);
    }

    @Test
    public void testChecksumSchema() throws Exception {
        HollowProducer p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = p.runCycle(ws -> {
            ws.add(new Top("One", "Two"));
        });

        HollowReadStateEngine r = new HollowReadStateEngine();
        HollowBlobReader br = new HollowBlobReader(r);
        br.readSnapshot(blobStore.retrieveSnapshotBlob(v1).getInputStream());

        HollowSetTypeReadState set = (HollowSetTypeReadState) r.getTypeState("SetOfBoxString");
        HollowMapTypeReadState map = (HollowMapTypeReadState) r.getTypeState("MapOfBoxStringToBoxString");

        // Pass with same schema but no key
        set.getChecksum(new HollowSetSchema("SetOfBoxString", "BoxString"));
        // Otherwise fail for mismatched schema
        try {
            set.getChecksum(new HollowSetSchema("SetOfBoxString", "BoxString", "v"));
            Assert.fail();
        } catch (Exception e) {}
        try {
            set.getChecksum(new HollowSetSchema("SetOfBoxString", "OtherBoxString"));
            Assert.fail();
        } catch (Exception e) {}
        try {
            set.getChecksum(map.getSchema());
            Assert.fail();
        } catch (Exception e) {}

        // Pass with same schema but no key
        map.getChecksum(new HollowMapSchema("MapOfBoxStringToBoxString", "BoxString", "BoxString"));
        // Otherwise fail for mismatched schema
        try {
            map.getChecksum(new HollowMapSchema("MapOfBoxStringToBoxString", "BoxString", "BoxString", "v"));
            Assert.fail();
        } catch (Exception e) {}
        try {
            map.getChecksum(new HollowMapSchema("MapOfBoxStringToBoxString", "OtherBoxString", "OtherBoxString"));
            Assert.fail();
        } catch (Exception e) {}
        try {
            map.getChecksum(set.getSchema());
            Assert.fail();
        } catch (Exception e) {}
    }

    /**
     * Tests that a client can override the hash key in the schema and use its own
     * HollowObjectHashCodeFinder when operating on sets or maps.
     * The HollowObjectHashCodeFinder returns bogus hash codes causing failure to find
     * elements or keys.
     */
    @Test
    public void testClientFail() throws Exception {
        HollowProducer p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = p.runCycle(ws -> {
            ws.add(new Top(IntStream.range(0, 100).mapToObj(Integer::toString).toArray(String[]::new)));
        });

        HollowReadStateEngine r = new HollowReadStateEngine(new CustomHashCodeFinder() {
            @Override public int hashCode(Object objectToHash) {
                // Hash differs from @HollowHashKey hash
                return 0;
            }
        });
        HollowBlobReader br = new HollowBlobReader(r);
        br.readSnapshot(blobStore.retrieveSnapshotBlob(v1).getInputStream());

        SetOfBoxString s = new SetOfBoxString(r, 0);
        Assert.assertTrue(IntStream.range(0, s.size()).
                anyMatch(i -> !s.contains(new BoxString(Integer.toString(i)))));

        MapOfBoxStringToBoxString m = new MapOfBoxStringToBoxString(r, 0);
        Assert.assertTrue(IntStream.range(0, m.size()).
                anyMatch(i -> !m.containsKey(new BoxString(Integer.toString(i)))));
    }

    /**
     * Tests that a client can override the hash key in the schema and use its own
     * HollowObjectHashCodeFinder when operating on sets or maps.
     */
    @Test
    public void testClient() throws Exception {
        testClient(true);
        testClient(false);
    }

    void testClient(boolean override) throws Exception {
        HollowProducer p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = p.runCycle(ws -> {
            ws.add(new Top(IntStream.range(0, 100).mapToObj(Integer::toString).toArray(String[]::new)));
        });

        HollowReadStateEngine r = override
                ? new HollowReadStateEngine(new CustomHashCodeFinder())
                : new HollowReadStateEngine();
        HollowBlobReader br = new HollowBlobReader(r);
        br.readSnapshot(blobStore.retrieveSnapshotBlob(v1).getInputStream());

        checkSchema(r, override);
        checkContent(r);

        long v2 = p.runCycle(ws -> {
            ws.add(new Top(IntStream.range(0, 101).mapToObj(Integer::toString).toArray(String[]::new)));
        });

        br.applyDelta(blobStore.retrieveDeltaBlob(v1).getInputStream());

        checkSchema(r, override);
        checkContent(r);
    }

    static void checkContent(HollowReadStateEngine r) {
        SetOfBoxString s = new SetOfBoxString(r, 1);
        Assert.assertTrue(IntStream.range(0, s.size()).
                allMatch(i -> s.contains(new BoxString(Integer.toString(i)))));

        MapOfBoxStringToBoxString m = new MapOfBoxStringToBoxString(r, 1);
        Assert.assertTrue(IntStream.range(0, m.size()).
                allMatch(i -> m.containsKey(new BoxString(Integer.toString(i)))));
    }

    static void checkSchema(HollowReadStateEngine r, boolean override) {
        for (HollowSchema s : r.getSchemas()) {
            if (s instanceof HollowSetSchema) {
                HollowSetSchema ss = (HollowSetSchema) s;
                if (override) {
                    Assert.assertNull(ss.getHashKey());
                } else {
                    Assert.assertNotNull(ss.getHashKey());
                }
            } else if (s instanceof HollowMapSchema) {
                HollowMapSchema ms = (HollowMapSchema) s;
                if (override) {
                    Assert.assertNull(ms.getHashKey());
                } else {
                    Assert.assertNotNull(ms.getHashKey());
                }
            }
        }
    }

    static class BoxString {
        @HollowInline String value;

        BoxString(String value) {
            this.value = value;
        }

        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            BoxString box = (BoxString) o;
            return Objects.equals(value, box.value);
        }

        @Override public int hashCode() {
            return Objects.hash(value);
        }
    }

    static class SetOfBoxString extends HollowSet<BoxString> {
        final HollowObjectTypeDataAccess daBoxString;

        SetOfBoxString(HollowReadStateEngine r, int ordinal) {
            super(getDelegate(r), ordinal);
            this.daBoxString = (HollowObjectTypeDataAccess) r.getTypeDataAccess("BoxString");
        }

        @Override public BoxString instantiateElement(int elementOrdinal) {
            String v = daBoxString.readString(elementOrdinal, 0);
            return new BoxString(v);
        }

        @Override public boolean equalsElement(int elementOrdinal, Object testObject) {
            BoxString v = instantiateElement(elementOrdinal);
            return v.equals(testObject);
        }

        static HollowSetLookupDelegate<BoxString> getDelegate(HollowReadStateEngine r) {
            return new HollowSetLookupDelegate<>((HollowSetTypeDataAccess) r.getTypeDataAccess("SetOfBoxString"));
        }
    }

    static class MapOfBoxStringToBoxString extends HollowMap<BoxString, BoxString> {
        final HollowObjectTypeDataAccess daBoxString;

        MapOfBoxStringToBoxString(HollowReadStateEngine r, int ordinal) {
            super(getDelegate(r), ordinal);
            this.daBoxString = (HollowObjectTypeDataAccess) r.getTypeDataAccess("BoxString");
        }

        @Override public BoxString instantiateKey(int keyOrdinal) {
            String v = daBoxString.readString(keyOrdinal, 0);
            return new BoxString(v);
        }

        @Override public BoxString instantiateValue(int valueOrdinal) {
            String v = daBoxString.readString(valueOrdinal, 0);
            return new BoxString(v);
        }

        @Override public boolean equalsKey(int keyOrdinal, Object testObject) {
            BoxString v = instantiateKey(keyOrdinal);
            return v.equals(testObject);
        }

        @Override public boolean equalsValue(int valueOrdinal, Object testObject) {
            BoxString v = instantiateValue(valueOrdinal);
            return v.equals(testObject);
        }

        static HollowMapLookupDelegate<BoxString, BoxString> getDelegate(HollowReadStateEngine r) {
            return new HollowMapLookupDelegate<>(
                    (HollowMapTypeDataAccess) r.getTypeDataAccess("MapOfBoxStringToBoxString"));
        }
    }

    static class BoxInt {
        int value;

        BoxInt(int value) {
            this.value = value;
        }

        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            BoxInt box = (BoxInt) o;
            return value == box.value;
        }

        @Override public int hashCode() {
            return Integer.hashCode(value);
        }
    }

    static class BoxLong {
        long value;

        BoxLong(long value) {
            this.value = value;
        }

        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            BoxLong box = (BoxLong) o;
            return value == box.value;
        }

        @Override public int hashCode() {
            return Long.hashCode(value);
        }
    }

    static class Top {
        final Set<BoxString> setOfStrings;
        final Map<BoxString, BoxString> mapOfStrings;

        final Set<BoxInt> setOfInts;
        final Map<BoxInt, BoxInt> mapOfInts;

        final Set<BoxLong> setOfLongs;
        final Map<BoxLong, BoxLong> mapOfLongs;

        Top(String... setOfStrings) {
            this.setOfStrings = Stream.of(setOfStrings).map(BoxString::new).collect(Collectors.toSet());
            this.mapOfStrings = Stream.of(setOfStrings).collect(toMap(BoxString::new, BoxString::new));

            this.setOfInts = Collections.emptySet();
            this.mapOfInts = Collections.emptyMap();

            this.setOfLongs = Collections.emptySet();
            this.mapOfLongs = Collections.emptyMap();
        }

        Top(List<String> strings, List<Integer> ints, List<Long> longs) {
            this.setOfStrings = strings.stream().map(BoxString::new).collect(Collectors.toSet());
            this.mapOfStrings = strings.stream().collect(toMap(BoxString::new, BoxString::new));

            this.setOfInts = ints.stream().map(BoxInt::new).collect(Collectors.toSet());
            this.mapOfInts = ints.stream().collect(toMap(BoxInt::new, BoxInt::new));

            this.setOfLongs = longs.stream().map(BoxLong::new).collect(Collectors.toSet());
            this.mapOfLongs = longs.stream().collect(toMap(BoxLong::new, BoxLong::new));
        }
    }

    static class TopWithOrdinals {
        @HollowHashKey(fields = {}) final Set<BoxString> strings;

        @HollowHashKey(fields = {}) final Map<BoxString, BoxString> map;

        TopWithOrdinals(String... strings) {
            this.strings = Stream.of(strings).map(BoxString::new).collect(Collectors.toSet());
            this.map = Stream.of(strings).collect(toMap(BoxString::new, BoxString::new));
        }
    }

    static class CustomHashCodeFinder implements HollowObjectHashCodeFinder {
        @Override
        public int hashCode(String typeName, int ordinal, Object objectToHash) {
            if (typeName.equals("BoxString")) {
                return ((BoxString) objectToHash).value.hashCode();
            } else if (typeName.equals("BoxInt")) {
                return ((BoxInt) objectToHash).value;
            } else if (typeName.equals("BoxLong")) {
                return Long.hashCode(((BoxLong) objectToHash).value);
            } else {
                return ordinal;
            }
        }

        @Override
        public int hashCode(Object objectToHash) {
            if (objectToHash instanceof BoxString) {
                return ((BoxString) objectToHash).value.hashCode();
            } else if (objectToHash instanceof BoxInt) {
                return Integer.hashCode(((BoxInt) objectToHash).value);
            } else if (objectToHash instanceof BoxLong) {
                return Long.hashCode(((BoxLong) objectToHash).value);
            } else {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public Set<String> getTypesWithDefinedHashCodes() {
            return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
                    "BoxString", "BoxInt", "BoxLong"
            )));
        }

        @Deprecated
        @Override
        public int hashCode(int ordinal, Object objectToHash) {
            throw new UnsupportedOperationException();
        }
    }
}