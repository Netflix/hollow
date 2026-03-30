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
package com.netflix.hollow.core.index;

import static org.junit.Assert.fail;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("unused")
public class HollowPrimaryKeyIndexTest extends AbstractStateEngineTest {

    protected TestableUniqueKeyIndex createIndex(String type, String ... fieldPaths) {
        return new HollowPrimaryKeyIndex(readStateEngine, type, fieldPaths);
    }

    protected TestableUniqueKeyIndex createIndex(ArraySegmentRecycler memoryRecycler, String type, String ... fieldPaths) {
        return new HollowPrimaryKeyIndex(readStateEngine, memoryRecycler, type, fieldPaths);
    }

    @Test
    public void testSnapshotAndDelta() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.add(new TypeA(1, 1.1d, new TypeB("one")));
        mapper.add(new TypeA(1, 1.1d, new TypeB("1")));
        mapper.add(new TypeA(2, 2.2d, new TypeB("two")));

        roundTripSnapshot();

        // Auto Discover fieldPaths from @HollowPrimaryKey
        // UniqueKeyIndex idx = createIndex("TypeA", "a1", "a2", "ab.b1.value");
        TestableUniqueKeyIndex idx = createIndex("TypeA");
        idx.listenForDeltaUpdates();

        int ord1 = idx.getMatchingOrdinal(1, 1.1d, "1");
        int ord0 = idx.getMatchingOrdinal(1, 1.1d, "one");
        int ord2 = idx.getMatchingOrdinal(2, 2.2d, "two");

        Assert.assertEquals(0, ord0);
        Assert.assertEquals(1, ord1);
        Assert.assertEquals(2, ord2);
        assertEquals(idx.getRecordKey(0), 1, 1.1d, "one");
        assertEquals(idx.getRecordKey(1), 1, 1.1d, "1");
        assertEquals(idx.getRecordKey(2), 2, 2.2d, "two");

        mapper.add(new TypeA(1, 1.1d, new TypeB("one")));
        // mapper.add(new TypeA(1, 1.1d, new TypeB("1")));
        mapper.add(new TypeA(2, 2.2d, new TypeB("two")));
        mapper.add(new TypeA(3, 3.3d, new TypeB("three")));

        roundTripDelta();

        ord0 = idx.getMatchingOrdinal(1, 1.1d, "one");
        ord1 = idx.getMatchingOrdinal(1, 1.1d, "1");
        ord2 = idx.getMatchingOrdinal(2, 2.2d, "two");
        int ord3 = idx.getMatchingOrdinal(3, 3.3d, "three");

        Assert.assertEquals(0, ord0);
        Assert.assertEquals(-1, ord1);
        Assert.assertEquals(2, ord2);
        Assert.assertEquals(3, ord3);
        assertEquals(idx.getRecordKey(0), 1, 1.1d, "one");
        assertEquals(idx.getRecordKey(1), 1, 1.1d, "1"); // it is a ghost record (marked deleted but it is available)
        assertEquals(idx.getRecordKey(2), 2, 2.2d, "two");
        assertEquals(idx.getRecordKey(3), 3, 3.3d, "three");
    }

    @Test
    public void testApproxHeapFootprint() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.add(new TypeA(1, 1.1d, new TypeB("one")));
        mapper.add(new TypeA(1, 1.1d, new TypeB("1")));
        mapper.add(new TypeA(2, 2.2d, new TypeB("two")));
        roundTripSnapshot();
        HollowPrimaryKeyIndex idx = (HollowPrimaryKeyIndex) createIndex("TypeA");
        Assert.assertTrue(idx.approxHeapFootprintInBytes() > 0);
    }

    @Test
    public void indicatesWhetherOrNotDuplicateKeysExist() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.add(new TypeA(1, 1.1d, new TypeB("one")));
        mapper.add(new TypeA(1, 1.1d, new TypeB("1")));
        mapper.add(new TypeA(2, 2.2d, new TypeB("two")));

        roundTripSnapshot();

        // Auto Discover fieldPaths from @HollowPrimaryKey
        //UniqueKeyIndex idx = createIndex("TypeA", "a1", "a2", "ab.b1.value");
        TestableUniqueKeyIndex idx = createIndex("TypeA");
        idx.listenForDeltaUpdates();

        Assert.assertFalse(idx.containsDuplicates());

        mapper.add(new TypeA(1, 1.1d, new TypeB("one")));
        mapper.add(new TypeA(1, 1.1d, new TypeB("1")));
        mapper.add(new TypeA(2, 2.2d, new TypeB("two")));
        mapper.add(new TypeA(2, 2.2d, new TypeB("two", true)));

        roundTripDelta();

        Assert.assertEquals(1, idx.getDuplicateKeys().size());
        Assert.assertTrue(idx.containsDuplicates());
    }

    @Test
    public void getDuplicateKeysWithMaxReturnsCorrectCounts() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.add(new TypeA(1, 1.1d, new TypeB("one", false)));
        mapper.add(new TypeA(1, 1.1d, new TypeB("one", true)));

        mapper.add(new TypeA(2, 2.2d, new TypeB("two", false)));
        mapper.add(new TypeA(2, 2.2d, new TypeB("two", true)));

        mapper.add(new TypeA(3, 3.3d, new TypeB("three", false)));
        mapper.add(new TypeA(3, 3.3d, new TypeB("three", true)));

        // Non-duplicate key
        mapper.add(new TypeA(4, 4.4d, new TypeB("four")));

        roundTripSnapshot();

        TestableUniqueKeyIndex idx = createIndex("TypeA");

        java.util.Collection<HollowPrimaryKeyIndex.DuplicateKeyInfo> allDuplicates = idx.getDuplicateKeys(Integer.MAX_VALUE);
        Assert.assertEquals(3, allDuplicates.size());

        for (HollowPrimaryKeyIndex.DuplicateKeyInfo info : allDuplicates) {
            Assert.assertEquals(2L, info.getCount());
        }
    }

    @Test
    public void getDuplicateKeysWithMaxRespectsLimit() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        // Create 5 different duplicate keys
        for (int i = 1; i <= 5; i++) {
            mapper.add(new TypeA(i, i * 1.1d, new TypeB("value" + i)));
            mapper.add(new TypeA(i, i * 1.1d, new TypeB("value" + i, true)));
        }

        roundTripSnapshot();

        TestableUniqueKeyIndex idx = createIndex("TypeA");

        java.util.Collection<HollowPrimaryKeyIndex.DuplicateKeyInfo> limited = idx.getDuplicateKeys(3);
        Assert.assertEquals(3, limited.size());

        java.util.Collection<HollowPrimaryKeyIndex.DuplicateKeyInfo> single = idx.getDuplicateKeys(1);
        Assert.assertEquals(1, single.size());

        java.util.Collection<HollowPrimaryKeyIndex.DuplicateKeyInfo> none = idx.getDuplicateKeys(0);
        Assert.assertEquals(0, none.size());

        java.util.Collection<HollowPrimaryKeyIndex.DuplicateKeyInfo> all = idx.getDuplicateKeys(Integer.MAX_VALUE);
        Assert.assertEquals(5, all.size());
    }

    @Test
    public void getDuplicateKeysWithMaxHandlesNoDuplicates() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.add(new TypeA(1, 1.1d, new TypeB("one")));
        mapper.add(new TypeA(2, 2.2d, new TypeB("two")));
        mapper.add(new TypeA(3, 3.3d, new TypeB("three")));

        roundTripSnapshot();

        TestableUniqueKeyIndex idx = createIndex("TypeA");

        java.util.Collection<HollowPrimaryKeyIndex.DuplicateKeyInfo> duplicates = idx.getDuplicateKeys(100);
        Assert.assertEquals(0, duplicates.size());
        Assert.assertFalse(idx.containsDuplicates());
    }

    @Test
    public void handlesEmptyTypes() throws IOException {
        HollowObjectSchema testSchema = new HollowObjectSchema("Test", 1);
        testSchema.addField("test1", FieldType.INT);

        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(testSchema));

        roundTripSnapshot();

        TestableUniqueKeyIndex idx = createIndex("Test", "test1");

        Assert.assertEquals(-1, idx.getMatchingOrdinal(100));
        Assert.assertFalse(idx.containsDuplicates());
    }

    @Test
    public void testSnapshotAndDeltaWithStateEngineMemoryRecycler() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.add(new TypeA(1, 1.1d, new TypeB("one")));
        mapper.add(new TypeA(1, 1.1d, new TypeB("1")));
        mapper.add(new TypeA(2, 2.2d, new TypeB("two")));

        roundTripSnapshot();

        TestableUniqueKeyIndex idx = createIndex(readStateEngine.getMemoryRecycler(), "TypeA", "a1", "a2", "ab.b1.value");
        idx.listenForDeltaUpdates();

        int ord1 = idx.getMatchingOrdinal(1, 1.1d, "1");
        int ord0 = idx.getMatchingOrdinal(1, 1.1d, "one");
        int ord2 = idx.getMatchingOrdinal(2, 2.2d, "two");

        Assert.assertEquals(0, ord0);
        Assert.assertEquals(1, ord1);
        Assert.assertEquals(2, ord2);
        assertEquals(idx.getRecordKey(0), 1, 1.1d, "one");
        assertEquals(idx.getRecordKey(1), 1, 1.1d, "1");
        assertEquals(idx.getRecordKey(2), 2, 2.2d, "two");

        mapper.add(new TypeA(1, 1.1d, new TypeB("one")));
        // mapper.add(new TypeA(1, 1.1d, new TypeB("1")));
        mapper.add(new TypeA(2, 2.2d, new TypeB("two")));
        mapper.add(new TypeA(3, 3.3d, new TypeB("three")));

        roundTripDelta();

        ord0 = idx.getMatchingOrdinal(1, 1.1d, "one");
        ord1 = idx.getMatchingOrdinal(1, 1.1d, "1");
        ord2 = idx.getMatchingOrdinal(2, 2.2d, "two");
        int ord3 = idx.getMatchingOrdinal(3, 3.3d, "three");

        Assert.assertEquals(0, ord0);
        Assert.assertEquals(-1, ord1);
        Assert.assertEquals(2, ord2);
        Assert.assertEquals(3, ord3);
        assertEquals(idx.getRecordKey(0), 1, 1.1d, "one");
        assertEquals(idx.getRecordKey(1), 1, 1.1d, "1"); // it is a ghost record (marked deleted but it is available)
        assertEquals(idx.getRecordKey(2), 2, 2.2d, "two");
        assertEquals(idx.getRecordKey(3), 3, 3.3d, "three");
    }

    @Test
    public void testDups() throws IOException {
        String typeA = "TypeA";
        int numOfItems = 1000;
        int a1ValueStart = 1;
        double a2Value = 1;
        addDataForDupTesting(writeStateEngine, a1ValueStart, a2Value, numOfItems);
        roundTripSnapshot();

        int a1Pos = ((HollowObjectSchema) readStateEngine.getTypeState(typeA).getSchema()).getPosition("a1");
        int a2Pos = ((HollowObjectSchema) readStateEngine.getTypeState(typeA).getSchema()).getPosition("a2");
        TestableUniqueKeyIndex idx = createIndex("TypeA", "a1");
        idx.listenForDeltaUpdates();
        Assert.assertFalse(idx.containsDuplicates());

        // add dups
        int numOfDups = (int) (numOfItems * 0.2);
        int a1dupValueStart = 2;
        int a1dupValueEnd = a1dupValueStart + numOfDups;
        double a2dupValues = 2;

        { // Add dups
            addDataForDupTesting(writeStateEngine, a1ValueStart, a2Value, numOfItems);
            addDataForDupTesting(writeStateEngine, a1dupValueStart, a2dupValues, numOfDups);
            roundTripDelta();
            Assert.assertEquals(true, idx.containsDuplicates()); // Make sure there is dups

            HollowObjectTypeReadState readTypeState = (HollowObjectTypeReadState) readStateEngine.getTypeState(typeA);
            for (int i = 0; i < readTypeState.maxOrdinal(); i++) {
                int a1Val = readTypeState.readInt(i, a1Pos);
                boolean isInDupRange = a1dupValueStart <= a1Val && a1Val < a1dupValueEnd;

                int ordinal = idx.getMatchingOrdinal(a1Val);
                double a2Val = readTypeState.readDouble(ordinal, a2Pos);
                //System.out.println("a1=" + a1Val + "\ta2=" + a2Val);

                if (isInDupRange) {
                    // Not deterministic
                    Assert.assertTrue(a2Val == a2Value || a2Val == a2dupValues);
                } else {
                    Assert.assertTrue(a2Val == a2Value);
                }
            }
        }

        { // remove dups
            addDataForDupTesting(writeStateEngine, a1ValueStart, a2Value, numOfItems);
            roundTripDelta();
            Assert.assertFalse(idx.containsDuplicates()); // Make sure there is no dups

            HollowObjectTypeReadState readTypeState = (HollowObjectTypeReadState) readStateEngine.getTypeState(typeA);
            for (int i = 0; i < readTypeState.maxOrdinal(); i++) {
                int a1Val = readTypeState.readInt(i, a1Pos);
                boolean isInDupRange = a1dupValueStart <= a1Val && a1Val < a1dupValueEnd;

                int ordinal = idx.getMatchingOrdinal(a1Val);
                double a2Val = readTypeState.readDouble(ordinal, a2Pos);
                // System.out.println("a1=" + a1Val + "\ta2=" + a2Val);

                // Should be equal to base value
                Assert.assertTrue(a2Val == a2Value);
            }
        }

        { // create dups
            addDataForDupTesting(writeStateEngine, a1ValueStart, a2Value, numOfItems);
            addDataForDupTesting(writeStateEngine, a1dupValueStart, a2dupValues, numOfDups);
            roundTripDelta();
            Assert.assertEquals(true, idx.containsDuplicates()); // Make sure there is dups

            HollowObjectTypeReadState readTypeState = (HollowObjectTypeReadState) readStateEngine.getTypeState(typeA);
            for (int i = 0; i < readTypeState.maxOrdinal(); i++) {
                int a1Val = readTypeState.readInt(i, a1Pos);
                boolean isInDupRange = a1dupValueStart <= a1Val && a1Val < a1dupValueEnd;

                int ordinal = idx.getMatchingOrdinal(a1Val);
                double a2Val = readTypeState.readDouble(ordinal, a2Pos);
                //System.out.println("a1=" + a1Val + "\ta2=" + a2Val);

                if (isInDupRange) {
                    // Not deterministic
                    Assert.assertTrue(a2Val == a2Value || a2Val == a2dupValues);
                } else {
                    Assert.assertTrue(a2Val == a2Value);
                }
            }
        }

        { // remove original
            addDataForDupTesting(writeStateEngine, a1dupValueStart, a2dupValues, numOfDups);
            roundTripDelta();
            Assert.assertFalse(idx.containsDuplicates()); // Make sure there is no dups

            HollowObjectTypeReadState readTypeState = (HollowObjectTypeReadState) readStateEngine.getTypeState(typeA);
            for (int i = 0; i < readTypeState.maxOrdinal(); i++) {
                int a1Val = readTypeState.readInt(i, a1Pos);
                boolean isInDupRange = a1dupValueStart <= a1Val && a1Val < a1dupValueEnd;

                int ordinal = idx.getMatchingOrdinal(a1Val);
                if (!isInDupRange) {
                    // should not be found if not in dup range
                    Assert.assertTrue(ordinal < 0);
                    continue;
                }

                double a2Val = readTypeState.readDouble(ordinal, a2Pos);
                // System.out.println("a1=" + a1Val + "\ta2=" + a2Val);

                // Make sure value is the Dup Values
                Assert.assertTrue(a2Val == a2dupValues);
            }
        }
    }

    @Test
    public void testNotBindable() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectSchema movieSchema = new HollowObjectSchema("Movie", 3);
        movieSchema.addField("id", HollowObjectSchema.FieldType.LONG);
        movieSchema.addField("title", HollowObjectSchema.FieldType.REFERENCE, "String");
        movieSchema.addField("releaseYear", HollowObjectSchema.FieldType.INT);
        HollowObjectTypeWriteState movieState = new HollowObjectTypeWriteState(movieSchema);
        writeEngine.addTypeState(movieState);

        HollowObjectWriteRecord movieRec = new HollowObjectWriteRecord(movieSchema);
        movieRec.setLong("id", 1);
        movieRec.setReference("title", 0);  // NOTE that String type wasn't added
        movieRec.setInt("releaseYear", 1999);
        writeEngine.add("Movie", movieRec);

        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(writeEngine, readEngine);

        // invalid because root type doesn't exist
        HollowPrimaryKeyIndex invalidPki1 = new HollowPrimaryKeyIndex(readEngine, "String", "value");
        try {
            invalidPki1.getMatchingOrdinal("test");
            fail("Index on root type not bound is expected to fail hard at query time");
        } catch (IllegalStateException e) {}

        // invalid because a type in the field paths doesn't exist
        HollowPrimaryKeyIndex invalidPki2 = new HollowPrimaryKeyIndex(readEngine, "Movie", "title.value");
        try {
            invalidPki2.getMatchingOrdinal(1L);
            fail("Index on field path not bound is expected to fail hard at query time");
        } catch (IllegalStateException e) {}

        // valid index despite a non-indexed field (title) not bindable to a type (String)
        HollowPrimaryKeyIndex validPki = new HollowPrimaryKeyIndex(readEngine, "Movie", "id");
        Assert.assertEquals(0, validPki.getMatchingOrdinal(1L));
    }

    @Test
    public void testNullPKeyIdx() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.add(new TypeNullPKey());
        roundTripSnapshot();

        try {
            HollowPrimaryKeyIndex invalidPkIdx = new HollowPrimaryKeyIndex(this.readStateEngine, "TypeNullPKey", "id");
            fail("Index on type with null fields is expected to fail construction");
        } catch (NullPointerException e) {}
    }

    private static void addDataForDupTesting(HollowWriteStateEngine writeStateEngine, int a1Start, double a2, int size) {
        TypeB typeB = new TypeB("commonTypeB");
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        int max = a1Start + size;
        for (int a1 = a1Start; a1 < max; a1++) {
            mapper.add(new TypeA(a1, a2, typeB));
        }
    }

    private static void assertEquals(Object[] actual, Object... expected) {
        Assert.assertEquals(actual.length, expected.length);
        for (int i = 0; i < actual.length; i++) {
            Assert.assertEquals(expected[i], actual[i]);
        }
    }


    @HollowPrimaryKey(fields = { "a1", "a2", "ab.b1" })
    private static class TypeA {
        private final int a1;
        private final double a2;
        private final TypeB ab;

        public TypeA(int a1, double a2, TypeB ab) {
            this.a1 = a1;
            this.a2 = a2;
            this.ab = ab;
        }
    }

    private static class TypeB {
        private final String b1;
        private final boolean isDuplicate;

        public TypeB(String b1) {
            this(b1, false);
        }

        public TypeB(String b1, boolean isDuplicate) {
            this.b1 = b1;
            this.isDuplicate = isDuplicate;
        }
    }

    @HollowPrimaryKey(fields = {"id"})
    private static class TypeNullPKey {
        private final Long id;

        public TypeNullPKey() {
            this.id = null;
        }
    }

    @Override
    protected void initializeTypeStates() { }

}
