/*
 *  Copyright 2016-2021 Netflix, Inc.
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
package com.netflix.hollow.core.write;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowBlobWriter;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HollowTypeWriteStatePartitionTest {

    @Test
    public void testSinglePartitionBackwardCompatibility() {
        // Create schema
        HollowObjectSchema schema = new HollowObjectSchema("Movie", 2, "id");
        schema.addField("id", HollowObjectSchema.FieldType.INT);
        schema.addField("title", HollowObjectSchema.FieldType.STRING);

        // Create write state with 1 partition (default)
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema);
        writeState.setStateEngine(createMockStateEngine());

        assertEquals(1, writeState.getNumPartitions());

        // Add some records
        writeState.prepareForNextCycle();
        for (int i = 0; i < 10; i++) {
            HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
            rec.setInt("id", i);
            rec.setString("title", "Movie " + i);
            writeState.add(rec);
        }

        // All should be in partition 0
        assertEquals(10, writeState.getPartition(0).getCurrentCyclePopulated().cardinality());
    }

    @Test
    public void testMultiplePartitionsWithPrimaryKey() {
        // Create schema with primary key
        HollowObjectSchema schema = new HollowObjectSchema("Movie", 2, "id");
        schema.addField("id", HollowObjectSchema.FieldType.INT);
        schema.addField("title", HollowObjectSchema.FieldType.STRING);

        // Create write state with 8 partitions
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema, -1, 8);
        writeState.setStateEngine(createMockStateEngine());
        writeState.setPrimaryKey(new PrimaryKey("Movie", "id"));

        assertEquals(8, writeState.getNumPartitions());

        // Add 100 records
        writeState.prepareForNextCycle();
        Map<Integer, Set<Integer>> partitionToIds = new HashMap<>();

        for (int i = 0; i < 100; i++) {
            HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
            rec.setInt("id", i);
            rec.setString("title", "Movie " + i);

            // Use optimized add with explicit primary key
            int ordinal = writeState.add(rec, i);

            // Determine which partition this ID should be in
            int expectedPartition = HollowTypeWriteStatePartitionSelector.selectPartition(8, i);

            // Track which partition has this ID
            partitionToIds.computeIfAbsent(expectedPartition, k -> new HashSet<>()).add(i);

            // Verify the record was added to the expected partition
            assertTrue("Record with id=" + i + " should be in partition " + expectedPartition,
                    writeState.getPartition(expectedPartition).getCurrentCyclePopulated().get(ordinal));
        }

        // Verify records are distributed across multiple partitions
        int partitionsUsed = 0;
        for (int i = 0; i < 8; i++) {
            if (writeState.getPartition(i).getCurrentCyclePopulated().cardinality() > 0) {
                partitionsUsed++;
            }
        }
        assertTrue("Records should be distributed across multiple partitions, but only " + partitionsUsed + " were used",
                partitionsUsed > 1);

        // Verify total count
        int totalRecords = 0;
        for (int i = 0; i < 8; i++) {
            totalRecords += writeState.getPartition(i).getCurrentCyclePopulated().cardinality();
        }
        assertEquals(100, totalRecords);
    }

    @Test
    public void testConsistentHashingSameKeyToSamePartition() {
        // Create schema
        HollowObjectSchema schema = new HollowObjectSchema("Movie", 2, "id");
        schema.addField("id", HollowObjectSchema.FieldType.INT);
        schema.addField("title", HollowObjectSchema.FieldType.STRING);

        // Create write state with 8 partitions
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema, -1, 8);
        writeState.setStateEngine(createMockStateEngine());
        writeState.setPrimaryKey(new PrimaryKey("Movie", "id"));

        writeState.prepareForNextCycle();

        // Add the same record multiple times (simulating updates)
        int testId = 42;
        Integer firstPartition = null;

        for (int attempt = 0; attempt < 5; attempt++) {
            HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
            rec.setInt("id", testId);
            rec.setString("title", "Movie " + testId + " v" + attempt);

            int ordinal = writeState.add(rec, testId);

            // Find which partition has this ordinal
            int partitionIndex = -1;
            for (int i = 0; i < 8; i++) {
                if (writeState.getPartition(i).getCurrentCyclePopulated().get(ordinal)) {
                    partitionIndex = i;
                    break;
                }
            }

            if (firstPartition == null) {
                firstPartition = partitionIndex;
            } else {
                // Same key should always go to same partition
                assertEquals("Records with same primary key should always map to same partition",
                        firstPartition.intValue(), partitionIndex);
            }
        }

        // Verify using the selector utility directly
        int expectedPartition = HollowTypeWriteStatePartitionSelector.selectPartition(8, testId);
        assertEquals(firstPartition.intValue(), expectedPartition);
    }

    @Test
    public void testPartitionSelectorUtility() {
        // Test that the selector utility gives consistent results

        // Test with single partition
        assertEquals(0, HollowTypeWriteStatePartitionSelector.selectPartition(1, 123));

        // Test with multiple partitions - same input should give same output
        int partition1 = HollowTypeWriteStatePartitionSelector.selectPartition(8, 42);
        int partition2 = HollowTypeWriteStatePartitionSelector.selectPartition(8, 42);
        assertEquals(partition1, partition2);

        // Test different keys give different partitions (at least sometimes)
        Set<Integer> partitions = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            int partition = HollowTypeWriteStatePartitionSelector.selectPartition(8, i);
            assertTrue("Partition index should be in valid range", partition >= 0 && partition < 8);
            partitions.add(partition);
        }
        assertTrue("100 different keys should use multiple partitions", partitions.size() > 1);
    }

    @Test
    public void testCompositePrimaryKey() {
        // Create schema with composite primary key
        HollowObjectSchema schema = new HollowObjectSchema("User", 3, "firstName", "lastName");
        schema.addField("firstName", HollowObjectSchema.FieldType.STRING);
        schema.addField("lastName", HollowObjectSchema.FieldType.STRING);
        schema.addField("age", HollowObjectSchema.FieldType.INT);

        // Create write state with 8 partitions
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema, -1, 8);
        writeState.setStateEngine(createMockStateEngine());
        writeState.setPrimaryKey(new PrimaryKey("User", "firstName", "lastName"));

        writeState.prepareForNextCycle();

        // Add records with composite keys
        String[][] users = {
            {"John", "Doe"},
            {"Jane", "Doe"},
            {"John", "Smith"},
            {"Jane", "Smith"}
        };

        Map<String, Integer> userToExpectedPartition = new HashMap<>();

        for (String[] user : users) {
            HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
            rec.setString("firstName", user[0]);
            rec.setString("lastName", user[1]);
            rec.setInt("age", 30);

            // Calculate expected partition before adding
            int expectedPartition = HollowTypeWriteStatePartitionSelector.selectPartition(8, user[0], user[1]);
            String key = user[0] + "|" + user[1];
            userToExpectedPartition.put(key, expectedPartition);

            // Use optimized add with composite primary key
            int ordinal = writeState.add(rec, user[0], user[1]);

            // Verify the record was added to the expected partition by checking that partition has the ordinal
            assertTrue("Partition " + expectedPartition + " should have ordinal " + ordinal,
                    writeState.getPartition(expectedPartition).getCurrentCyclePopulated().get(ordinal));
        }

        // Verify same composite key always goes to same partition
        for (String[] user : users) {
            int partition1 = HollowTypeWriteStatePartitionSelector.selectPartition(8, user[0], user[1]);
            int partition2 = HollowTypeWriteStatePartitionSelector.selectPartition(8, user[0], user[1]);
            assertEquals(partition1, partition2);

            String key = user[0] + "|" + user[1];
            assertEquals(userToExpectedPartition.get(key).intValue(), partition1);
        }
    }

    @Test
    public void testPartitionIndependentOrdinals() {
        // Verify that ordinals are scoped per partition
        HollowObjectSchema schema = new HollowObjectSchema("Movie", 2, "id");
        schema.addField("id", HollowObjectSchema.FieldType.INT);
        schema.addField("title", HollowObjectSchema.FieldType.STRING);

        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema, -1, 8);
        writeState.setStateEngine(createMockStateEngine());
        writeState.setPrimaryKey(new PrimaryKey("Movie", "id"));

        writeState.prepareForNextCycle();

        // Add enough records to ensure multiple partitions get multiple records
        for (int i = 0; i < 100; i++) {
            HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
            rec.setInt("id", i);
            rec.setString("title", "Movie " + i);
            writeState.add(rec, i);
        }

        // Check that multiple partitions have ordinal 0
        int partitionsWithOrdinal0 = 0;
        for (int i = 0; i < 8; i++) {
            if (writeState.getPartition(i).getCurrentCyclePopulated().get(0)) {
                partitionsWithOrdinal0++;
            }
        }

        assertTrue("Multiple partitions should have ordinal 0 (ordinals are scoped per partition)",
                partitionsWithOrdinal0 > 1);
    }

    @Test
    public void testBackwardCompatibilityWithMultiplePartitions() throws Exception {
        // Create schema
        HollowObjectSchema schema = new HollowObjectSchema("Movie", 2, "id");
        schema.addField("id", HollowObjectSchema.FieldType.INT);
        schema.addField("title", HollowObjectSchema.FieldType.STRING);

        // Create write state with 8 partitions
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema, -1, 8);
        writeState.setStateEngine(createMockStateEngine());
        writeState.setPrimaryKey(new PrimaryKey("Movie", "id"));

        writeState.prepareForNextCycle();

        // Add 100 records
        for (int i = 0; i < 100; i++) {
            HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
            rec.setInt("id", i);
            rec.setString("title", "Movie " + i);
            writeState.add(rec, i);
        }

        // Write to blob
        HollowWriteStateEngine engine = createMockStateEngine();
        engine.addTypeState(writeState);
        HollowBlobWriter writer = new HollowBlobWriter(engine);

        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        writer.writeSnapshot(baos);

        // Read back with HollowReadStateEngine
        com.netflix.hollow.core.read.engine.HollowReadStateEngine readEngine =
            new com.netflix.hollow.core.read.engine.HollowReadStateEngine();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        HollowBlobReader reader = new HollowBlobReader(readEngine);
        reader.readSnapshot(bais);

        // Verify backward compatibility: old consumers should be able to access data via shardsVolatile
        com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState readState =
            (com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState) readEngine.getTypeState("Movie");

        // Verify numShards() works (uses shardsVolatile internally - should not throw NPE)
        assertTrue("numShards should return a positive value", readState.numShards() > 0);

        // Verify that we can read records (new partition-aware consumers)
        assertTrue("Should have at least some records", readState.maxOrdinal() >= 0);
    }

    private HollowWriteStateEngine createMockStateEngine() {
        HollowWriteStateEngine engine = new HollowWriteStateEngine();
        return engine;
    }
}
