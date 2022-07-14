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

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("unused")
public class HollowPrimaryKeyIndexTest extends AbstractStateEngineTest {

    @Test
    public void testSnapshotAndDelta() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.add(new TypeA(1, 1.1d, new TypeB("one")));
        mapper.add(new TypeA(1, 1.1d, new TypeB("1")));
        mapper.add(new TypeA(2, 2.2d, new TypeB("two")));

        roundTripSnapshot();

        // Auto Discover fieldPaths from @HollowPrimaryKey
        // HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(readStateEngine, "TypeA", "a1", "a2", "ab.b1.value");
        HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(readStateEngine, "TypeA");
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
    public void indicatesWhetherOrNotDuplicateKeysExist() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.add(new TypeA(1, 1.1d, new TypeB("one")));
        mapper.add(new TypeA(1, 1.1d, new TypeB("1")));
        mapper.add(new TypeA(2, 2.2d, new TypeB("two")));

        roundTripSnapshot();

        // Auto Discover fieldPaths from @HollowPrimaryKey
        //HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(readStateEngine, "TypeA", "a1", "a2", "ab.b1.value");
        HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(readStateEngine, "TypeA");
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
    public void handlesEmptyTypes() throws IOException {
        HollowObjectSchema testSchema = new HollowObjectSchema("Test", 1);
        testSchema.addField("test1", FieldType.INT);

        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(testSchema));

        roundTripSnapshot();

        HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(readStateEngine, "Test", "test1");

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

        HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(readStateEngine, readStateEngine.getMemoryRecycler(), "TypeA", "a1", "a2", "ab.b1.value");
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
        HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(readStateEngine, "TypeA", "a1");
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
            for(int i = 0; i < readTypeState.maxOrdinal(); i++) {
                int a1Val = readTypeState.readInt(i, a1Pos);
                boolean isInDupRange = a1dupValueStart <= a1Val && a1Val < a1dupValueEnd;

                int ordinal = idx.getMatchingOrdinal(a1Val);
                double a2Val = readTypeState.readDouble(ordinal, a2Pos);
                //System.out.println("a1=" + a1Val + "\ta2=" + a2Val);

                if(isInDupRange) {
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
            for(int i = 0; i < readTypeState.maxOrdinal(); i++) {
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
            for(int i = 0; i < readTypeState.maxOrdinal(); i++) {
                int a1Val = readTypeState.readInt(i, a1Pos);
                boolean isInDupRange = a1dupValueStart <= a1Val && a1Val < a1dupValueEnd;

                int ordinal = idx.getMatchingOrdinal(a1Val);
                double a2Val = readTypeState.readDouble(ordinal, a2Pos);
                //System.out.println("a1=" + a1Val + "\ta2=" + a2Val);

                if(isInDupRange) {
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
            for(int i = 0; i < readTypeState.maxOrdinal(); i++) {
                int a1Val = readTypeState.readInt(i, a1Pos);
                boolean isInDupRange = a1dupValueStart <= a1Val && a1Val < a1dupValueEnd;

                int ordinal = idx.getMatchingOrdinal(a1Val);
                if(!isInDupRange) {
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

    private static void addDataForDupTesting(HollowWriteStateEngine writeStateEngine, int a1Start, double a2, int size) {
        TypeB typeB = new TypeB("commonTypeB");
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        int max = a1Start + size;
        for(int a1 = a1Start; a1 < max; a1++) {
            mapper.add(new TypeA(a1, a2, typeB));
        }
    }

    private static void assertEquals(Object[] actual, Object... expected) {
        Assert.assertEquals(actual.length, expected.length);
        for(int i = 0; i < actual.length; i++) {
            Assert.assertEquals(expected[i], actual[i]);
        }
    }


    @HollowPrimaryKey(fields = {"a1", "a2", "ab.b1"})
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

    @Override
    protected void initializeTypeStates() {
    }

}
