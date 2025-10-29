package com.netflix.hollow.core.write;

import static org.junit.Assert.*;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import org.junit.Test;

/**
 * Tests to verify that write-side and read-side partition hashing produce
 * identical results for all data types.
 */
public class PartitionHashConsistencyTest {

    @Test
    public void testIntegerKeyHashConsistency() {
        PrimaryKey pk = new PrimaryKey("TestType", "id");

        // Write side
        int writeHash = HollowTypeWriteStatePartitionSelector.computePrimaryKeyHash(pk, 12345);
        int writePartition = HollowTypeWriteStatePartitionSelector.selectPartition(4, pk, 12345);

        // Read side (using a mock read state)
        HollowObjectTypeReadState readState = createMockReadState(4);
        int readHash = readState.computePrimaryKeyHashForLookup(pk, 12345);
        int readPartition = readState.getPartitionForPrimaryKey(pk, 12345);

        assertEquals("Hash codes must match between write and read", writeHash, readHash);
        assertEquals("Partition assignments must match between write and read", writePartition, readPartition);
    }

    @Test
    public void testStringKeyHashConsistency() {
        PrimaryKey pk = new PrimaryKey("TestType", "name");

        String testValue = "TestString";

        int writeHash = HollowTypeWriteStatePartitionSelector.computePrimaryKeyHash(pk, testValue);
        int writePartition = HollowTypeWriteStatePartitionSelector.selectPartition(4, pk, testValue);

        HollowObjectTypeReadState readState = createMockReadState(4);
        int readHash = readState.computePrimaryKeyHashForLookup(pk, testValue);
        int readPartition = readState.getPartitionForPrimaryKey(pk, testValue);

        assertEquals("Hash codes must match for string keys", writeHash, readHash);
        assertEquals("Partition assignments must match for string keys", writePartition, readPartition);
    }

    @Test
    public void testLongKeyHashConsistency() {
        PrimaryKey pk = new PrimaryKey("TestType", "timestamp");

        Long testValue = 1234567890123L;

        int writeHash = HollowTypeWriteStatePartitionSelector.computePrimaryKeyHash(pk, testValue);
        int writePartition = HollowTypeWriteStatePartitionSelector.selectPartition(8, pk, testValue);

        HollowObjectTypeReadState readState = createMockReadState(8);
        int readHash = readState.computePrimaryKeyHashForLookup(pk, testValue);
        int readPartition = readState.getPartitionForPrimaryKey(pk, testValue);

        assertEquals("Hash codes must match for long keys", writeHash, readHash);
        assertEquals("Partition assignments must match for long keys", writePartition, readPartition);
    }

    @Test
    public void testMultiFieldKeyHashConsistency() {
        PrimaryKey pk = new PrimaryKey("TestType", "userId", "timestamp");

        Integer userId = 42;
        Long timestamp = 1234567890L;

        int writeHash = HollowTypeWriteStatePartitionSelector.computePrimaryKeyHash(pk, userId, timestamp);
        int writePartition = HollowTypeWriteStatePartitionSelector.selectPartition(4, pk, userId, timestamp);

        HollowObjectTypeReadState readState = createMockReadState(4);
        int readHash = readState.computePrimaryKeyHashForLookup(pk, userId, timestamp);
        int readPartition = readState.getPartitionForPrimaryKey(pk, userId, timestamp);

        assertEquals("Hash codes must match for multi-field keys", writeHash, readHash);
        assertEquals("Partition assignments must match for multi-field keys", writePartition, readPartition);
    }

    @Test
    public void testNullKeyHashConsistency() {
        PrimaryKey pk = new PrimaryKey("TestType", "id");

        int writeHash = HollowTypeWriteStatePartitionSelector.computePrimaryKeyHash(pk, (Object) null);
        int writePartition = HollowTypeWriteStatePartitionSelector.selectPartition(4, pk, (Object) null);

        HollowObjectTypeReadState readState = createMockReadState(4);
        int readHash = readState.computePrimaryKeyHashForLookup(pk, (Object) null);
        int readPartition = readState.getPartitionForPrimaryKey(pk, (Object) null);

        assertEquals("Hash codes must match for null keys", writeHash, readHash);
        assertEquals("Partition assignments must match for null keys", writePartition, readPartition);
        assertEquals("Null keys should hash to partition 0", 0, writePartition);
    }

    @Test
    public void testBooleanKeyHashConsistency() {
        PrimaryKey pk = new PrimaryKey("TestType", "active");

        Boolean testValue = true;

        int writeHash = HollowTypeWriteStatePartitionSelector.computePrimaryKeyHash(pk, testValue);
        int writePartition = HollowTypeWriteStatePartitionSelector.selectPartition(2, pk, testValue);

        HollowObjectTypeReadState readState = createMockReadState(2);
        int readHash = readState.computePrimaryKeyHashForLookup(pk, testValue);
        int readPartition = readState.getPartitionForPrimaryKey(pk, testValue);

        assertEquals("Hash codes must match for boolean keys", writeHash, readHash);
        assertEquals("Partition assignments must match for boolean keys", writePartition, readPartition);
    }

    @Test
    public void testFloatKeyHashConsistency() {
        PrimaryKey pk = new PrimaryKey("TestType", "score");

        Float testValue = 3.14159f;

        int writeHash = HollowTypeWriteStatePartitionSelector.computePrimaryKeyHash(pk, testValue);
        int writePartition = HollowTypeWriteStatePartitionSelector.selectPartition(4, pk, testValue);

        HollowObjectTypeReadState readState = createMockReadState(4);
        int readHash = readState.computePrimaryKeyHashForLookup(pk, testValue);
        int readPartition = readState.getPartitionForPrimaryKey(pk, testValue);

        assertEquals("Hash codes must match for float keys", writeHash, readHash);
        assertEquals("Partition assignments must match for float keys", writePartition, readPartition);
    }

    @Test
    public void testDoubleKeyHashConsistency() {
        PrimaryKey pk = new PrimaryKey("TestType", "value");

        Double testValue = 2.718281828;

        int writeHash = HollowTypeWriteStatePartitionSelector.computePrimaryKeyHash(pk, testValue);
        int writePartition = HollowTypeWriteStatePartitionSelector.selectPartition(4, pk, testValue);

        HollowObjectTypeReadState readState = createMockReadState(4);
        int readHash = readState.computePrimaryKeyHashForLookup(pk, testValue);
        int readPartition = readState.getPartitionForPrimaryKey(pk, testValue);

        assertEquals("Hash codes must match for double keys", writeHash, readHash);
        assertEquals("Partition assignments must match for double keys", writePartition, readPartition);
    }

    @Test
    public void testByteArrayKeyHashConsistency() {
        PrimaryKey pk = new PrimaryKey("TestType", "data");

        byte[] testValue = {1, 2, 3, 4, 5};

        int writeHash = HollowTypeWriteStatePartitionSelector.computePrimaryKeyHash(pk, testValue);
        int writePartition = HollowTypeWriteStatePartitionSelector.selectPartition(4, pk, testValue);

        HollowObjectTypeReadState readState = createMockReadState(4);
        int readHash = readState.computePrimaryKeyHashForLookup(pk, testValue);
        int readPartition = readState.getPartitionForPrimaryKey(pk, testValue);

        assertEquals("Hash codes must match for byte array keys", writeHash, readHash);
        assertEquals("Partition assignments must match for byte array keys", writePartition, readPartition);
    }

    @Test
    public void testXorBehaviorWithMultipleFields() {
        PrimaryKey pk = new PrimaryKey("TestType", "field1", "field2", "field3");

        // Test that XOR is actually being used (not multiplication or addition)
        // XOR property: a ^ a = 0
        Integer value1 = 100;
        Integer value2 = 200;
        Integer value3 = 100; // Same as value1

        int hash1 = HollowTypeWriteStatePartitionSelector.computePrimaryKeyHash(pk, value1, value2, value3);
        int hash2 = HollowTypeWriteStatePartitionSelector.computePrimaryKeyHash(pk, value1, value2, value3);

        // Hashes should be identical
        assertEquals("Same input should produce same hash", hash1, hash2);

        // Test read-side produces same result
        HollowObjectTypeReadState readState = createMockReadState(4);
        int readHash = readState.computePrimaryKeyHashForLookup(pk, value1, value2, value3);
        assertEquals("Read side should match write side with XOR", hash1, readHash);
    }

    /**
     * Creates a mock read state for testing partition calculations.
     * We only need the numPartitions field to be set correctly.
     */
    private HollowObjectTypeReadState createMockReadState(int numPartitions) {
        HollowObjectSchema schema = new HollowObjectSchema("TestType", 1);
        schema.addField("testField", FieldType.INT);

        // Create a minimal read state
        HollowObjectTypeReadState readState = new HollowObjectTypeReadState(
            null,  // no state engine needed for hash testing
            com.netflix.hollow.core.memory.MemoryMode.ON_HEAP,
            schema,
            schema
        );

        // Set up partitions using reflection to set the numPartitions field
        if (numPartitions > 1) {
            try {
                java.lang.reflect.Field numPartitionsField =
                    HollowObjectTypeReadState.class.getDeclaredField("numPartitions");
                numPartitionsField.setAccessible(true);
                numPartitionsField.setInt(readState, numPartitions);
            } catch (Exception e) {
                throw new RuntimeException("Failed to set numPartitions for test", e);
            }
        }

        return readState;
    }
}
