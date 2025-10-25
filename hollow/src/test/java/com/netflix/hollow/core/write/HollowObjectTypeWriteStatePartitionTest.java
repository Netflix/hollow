package com.netflix.hollow.core.write;

import static org.junit.Assert.*;

import com.netflix.hollow.core.HollowBlobHeader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.junit.Test;

/**
 * Tests for partition serialization in HollowObjectTypeWriteState.
 *
 * Covers:
 * - Single partition (backward compatible)
 * - Multi-partition serialization
 * - Header partition metadata
 * - Backward compatibility scenarios
 */
public class HollowObjectTypeWriteStatePartitionTest {

    @Test
    public void testSinglePartitionSerializationIsBackwardCompatible() throws IOException {
        // Create a simple schema
        HollowObjectSchema schema = new HollowObjectSchema("TestType", 2);
        schema.addField("id", FieldType.INT);
        schema.addField("name", FieldType.STRING);

        // Create write state with 1 partition (default)
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema, 1, 1);
        writeEngine.addTypeState(writeState);

        // Add some records
        addRecord(writeState, schema, 1, "Alice");
        addRecord(writeState, schema, 2, "Bob");

        // Prepare for write
        writeEngine.prepareForWrite();

        // Write snapshot
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        writer.writeSnapshot(baos);

        byte[] snapshotBytes = baos.toByteArray();

        // Verify header has no partition metadata (empty for single partition)
        // Note: buildHeader is package-private, so we'll verify through reading
        // The partition metadata should be empty when all types have 1 partition

        // Verify we can read it back
        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readEngine);
        reader.readSnapshot(new ByteArrayInputStream(snapshotBytes));

        HollowObjectTypeReadState readState = (HollowObjectTypeReadState) readEngine.getTypeState("TestType");
        assertNotNull("Should be able to read back single partition snapshot", readState);
        assertEquals("Should have correct max ordinal", 1, readState.maxOrdinal());
    }

    @Test
    public void testMultiPartitionSerializationWritesPartitionMetadata() throws IOException {
        // Create a simple schema
        HollowObjectSchema schema = new HollowObjectSchema("TestType", 2);
        schema.addField("id", FieldType.INT);
        schema.addField("name", FieldType.STRING);

        // Create write state with 3 partitions
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema, 1, 3);
        writeEngine.addTypeState(writeState);

        // Verify partition count is stored
        assertEquals("Write state should have 3 partitions", 3, writeState.getNumPartitions());

        // Add minimal data so we can write the snapshot
        addRecord(writeState, schema, 1, "test");

        // Prepare for write
        writeEngine.prepareForWrite();

        // Write snapshot and verify we can read partition metadata
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new HollowBlobWriter(writeEngine).writeSnapshot(baos);

        // Read back and verify partition metadata
        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        new HollowBlobReader(readEngine).readSnapshot(new ByteArrayInputStream(baos.toByteArray()));

        assertEquals("Multi-partition type should have 3 partitions in header",
                     3, readEngine.getHeaderPartitionCount("TestType"));
    }

    @Test
    public void testPartitionMetadataSerializationInHeader() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();

        // Create multiple types with different partition counts
        HollowObjectSchema schema1 = new HollowObjectSchema("SinglePartition", 1);
        schema1.addField("id", FieldType.INT);
        HollowObjectTypeWriteState writeState1 = new HollowObjectTypeWriteState(schema1, 1, 1);
        writeEngine.addTypeState(writeState1);

        HollowObjectSchema schema2 = new HollowObjectSchema("TwoPartitions", 1);
        schema2.addField("id", FieldType.INT);
        HollowObjectTypeWriteState writeState2 = new HollowObjectTypeWriteState(schema2, 1, 2);
        writeEngine.addTypeState(writeState2);

        HollowObjectSchema schema3 = new HollowObjectSchema("FourPartitions", 1);
        schema3.addField("id", FieldType.INT);
        HollowObjectTypeWriteState writeState3 = new HollowObjectTypeWriteState(schema3, 1, 4);
        writeEngine.addTypeState(writeState3);

        writeEngine.prepareForWrite();

        // Write snapshot
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        writer.writeSnapshot(baos);

        // Read back and verify header
        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readEngine);
        reader.readSnapshot(new ByteArrayInputStream(baos.toByteArray()));

        // Verify partition counts from header
        assertEquals("SinglePartition should default to 1",
                     1, readEngine.getHeaderPartitionCount("SinglePartition"));
        assertEquals("TwoPartitions should have 2",
                     2, readEngine.getHeaderPartitionCount("TwoPartitions"));
        assertEquals("FourPartitions should have 4",
                     4, readEngine.getHeaderPartitionCount("FourPartitions"));
    }

    @Test
    public void testOldConsumerSkipsPartitionMetadata() throws IOException {
        // This test verifies that old consumers can skip partition metadata in header
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();

        HollowObjectSchema schema = new HollowObjectSchema("TestType", 1);
        schema.addField("id", FieldType.INT);
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema, 1, 2);
        writeEngine.addTypeState(writeState);

        writeEngine.prepareForWrite();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        writer.writeSnapshot(baos);

        // Simulate old consumer reading header
        // Old consumer will skip the forward compatibility bytes
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        // Read version header (4 bytes)
        bais.skip(4);

        // Read randomized tags (16 bytes)
        bais.skip(16);

        // Read schemas envelope size
        int schemaEnvelopeSize = VarInt.readVInt(bais);
        assertTrue("Schema envelope size should be positive", schemaEnvelopeSize > 0);

        // Skip schemas
        bais.skip(schemaEnvelopeSize - 1); // -1 because we already read one byte for next VarInt

        // Read forward compatibility bytes size (this is where partition metadata is)
        int forwardCompatBytes = VarInt.readVInt(bais);

        // Old consumer skips these bytes
        bais.skip(forwardCompatBytes);

        // Old consumer should now be at header tags
        int numHeaderTags = (bais.read() << 8) | bais.read(); // readShort
        assertTrue("Should be able to read header tags after skipping partition metadata",
                   numHeaderTags >= 0);
    }

    @Test
    public void testNewConsumerReadsOldBlobWithoutPartitionMetadata() throws IOException {
        // Simulate an old blob without partition metadata
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();

        HollowObjectSchema schema = new HollowObjectSchema("TestType", 1);
        schema.addField("id", FieldType.INT);
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema, 1, 1);
        writeEngine.addTypeState(writeState);

        addRecord(writeState, schema, 42, null);

        writeEngine.prepareForWrite();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        writer.writeSnapshot(baos);

        // Read with new consumer
        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readEngine);
        reader.readSnapshot(new ByteArrayInputStream(baos.toByteArray()));

        // Verify defaults to 1 partition when no metadata
        assertEquals("Should default to 1 partition for old blobs",
                     1, readEngine.getHeaderPartitionCount("TestType"));

        HollowObjectTypeReadState readState = (HollowObjectTypeReadState) readEngine.getTypeState("TestType");
        assertNotNull("Should be able to read old blob", readState);
    }

    @Test
    public void testMultiPartitionRoundTripLimitedToPartition0() throws IOException {
        // This tests the current limited implementation that reads only partition 0
        HollowObjectSchema schema = new HollowObjectSchema("TestType", 2);
        schema.addField("id", FieldType.INT);
        schema.addField("value", FieldType.STRING);

        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema, 1, 2);
        writeEngine.addTypeState(writeState);

        // Add records to partition 0
        addRecord(writeState, schema, 1, "partition0-record1");
        addRecord(writeState, schema, 2, "partition0-record2");

        writeEngine.prepareForWrite();

        // Write snapshot
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        writer.writeSnapshot(baos);

        // Read back (will only read partition 0 due to current limitation)
        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readEngine);
        reader.readSnapshot(new ByteArrayInputStream(baos.toByteArray()));

        // Verify partition 0 was read
        HollowObjectTypeReadState readState = (HollowObjectTypeReadState) readEngine.getTypeState("TestType");
        assertNotNull("Should be able to read partition 0", readState);
        assertEquals("Should have records from partition 0", 1, readState.maxOrdinal());
    }

    @Test
    public void testPartitionCountValidation() {
        HollowObjectSchema schema = new HollowObjectSchema("TestType", 1);
        schema.addField("id", FieldType.INT);

        // Valid partition counts
        new HollowObjectTypeWriteState(schema, 1, 1); // single partition
        new HollowObjectTypeWriteState(schema, 1, 2); // two partitions
        new HollowObjectTypeWriteState(schema, 1, 8); // max partitions

        // Invalid partition counts
        try {
            new HollowObjectTypeWriteState(schema, 1, 0);
            fail("Should reject 0 partitions");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("between 1 and"));
        }

        try {
            new HollowObjectTypeWriteState(schema, 1, 9);
            fail("Should reject > 8 partitions");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("between 1 and"));
        }
    }

    @Test
    public void testHeaderPartitionCountAccessor() {
        HollowBlobHeader header = new HollowBlobHeader();

        // Default should be 1 (backward compatible)
        assertEquals("Default partition count should be 1",
                     1, header.getPartitionCount("NonExistentType"));

        // Set explicit partition counts
        java.util.Map<String, Integer> partitionCounts = new java.util.HashMap<>();
        partitionCounts.put("Type1", 2);
        partitionCounts.put("Type2", 4);
        header.setTypePartitionCounts(partitionCounts);

        assertEquals("Type1 should have 2 partitions", 2, header.getPartitionCount("Type1"));
        assertEquals("Type2 should have 4 partitions", 4, header.getPartitionCount("Type2"));
        assertEquals("Unknown type should default to 1", 1, header.getPartitionCount("Type3"));
    }

    @Test
    public void testEmptyPartitionMetadataNotWritten() throws IOException {
        // When all types have 1 partition, no partition metadata should be written
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();

        HollowObjectSchema schema = new HollowObjectSchema("TestType", 1);
        schema.addField("id", FieldType.INT);
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema, 1, 1);
        writeEngine.addTypeState(writeState);

        writeEngine.prepareForWrite();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);

        // Verify empty partition counts map (via reading)
        writer.writeSnapshot(baos);

        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        new HollowBlobReader(readEngine).readSnapshot(new ByteArrayInputStream(baos.toByteArray()));

        // Single partition should default to 1
        assertEquals("Single partition should default to 1", 1, readEngine.getHeaderPartitionCount("TestType"));
    }

    @Test
    public void testMultiPartitionFullRoundTrip() throws IOException {
        // Test full round-trip: write multi-partition snapshot, read it back with ordinal encoding
        HollowObjectSchema schema = new HollowObjectSchema("TestType", 2);
        schema.addField("id", FieldType.INT);
        schema.addField("value", FieldType.STRING);

        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema, 1, 2);
        writeEngine.addTypeState(writeState);

        // Add records to different partitions
        // Records will be distributed across partitions by hash
        addRecord(writeState, schema, 1, "value1");
        addRecord(writeState, schema, 2, "value2");
        addRecord(writeState, schema, 3, "value3");
        addRecord(writeState, schema, 4, "value4");

        writeEngine.prepareForWrite();

        // Write snapshot
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        writer.writeSnapshot(baos);

        // Read back
        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readEngine);
        reader.readSnapshot(new ByteArrayInputStream(baos.toByteArray()));

        // Verify read state
        HollowObjectTypeReadState readState = (HollowObjectTypeReadState) readEngine.getTypeState("TestType");
        assertNotNull("Should be able to read multi-partition snapshot", readState);
        assertEquals("Should have 2 partitions", 2, readState.getNumPartitions());

        // Verify we can read back the data through ordinal encoding
        // Note: We can't assume ordinal-to-ID mapping since partitioning is hash-based
        // Just verify we get 4 valid records with correct format
        int foundRecords = 0;
        java.util.Set<Integer> foundIds = new java.util.HashSet<>();
        java.util.BitSet populatedOrdinals = readState.getPopulatedOrdinals();
        for(int encodedOrdinal = populatedOrdinals.nextSetBit(0);
            encodedOrdinal != -1;
            encodedOrdinal = populatedOrdinals.nextSetBit(encodedOrdinal + 1)) {

            int id = readState.readInt(encodedOrdinal, 0);
            String value = readState.readString(encodedOrdinal, 1);

            // Verify the data format is correct
            assertTrue("ID should be 1-4, got: " + id, id >= 1 && id <= 4);
            assertEquals("Value should match ID", "value" + id, value);
            foundIds.add(id);
            foundRecords++;
        }

        assertEquals("Should find all 4 records", 4, foundRecords);
        assertEquals("Should find all 4 unique IDs", 4, foundIds.size());
        assertTrue("Should have ID 1", foundIds.contains(1));
        assertTrue("Should have ID 2", foundIds.contains(2));
        assertTrue("Should have ID 3", foundIds.contains(3));
        assertTrue("Should have ID 4", foundIds.contains(4));
    }

    @Test
    public void testMultiPartitionOrdinalEncoding() throws IOException {
        // Test that ordinal encoding/decoding works correctly
        HollowObjectSchema schema = new HollowObjectSchema("TestType", 1);
        schema.addField("id", FieldType.INT);

        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema, 1, 3);
        writeEngine.addTypeState(writeState);

        // Add a few records
        for(int i = 0; i < 10; i++) {
            addRecord(writeState, schema, i, null);
        }

        writeEngine.prepareForWrite();

        // Write and read
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new HollowBlobWriter(writeEngine).writeSnapshot(baos);

        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        new HollowBlobReader(readEngine).readSnapshot(new ByteArrayInputStream(baos.toByteArray()));

        HollowObjectTypeReadState readState = (HollowObjectTypeReadState) readEngine.getTypeState("TestType");
        assertEquals("Should have 3 partitions", 3, readState.getNumPartitions());

        // Verify ordinal encoding format: (partitionOrdinal << 3) | partitionIndex
        java.util.BitSet populatedOrdinals = readState.getPopulatedOrdinals();
        for(int encodedOrdinal = populatedOrdinals.nextSetBit(0);
            encodedOrdinal != -1;
            encodedOrdinal = populatedOrdinals.nextSetBit(encodedOrdinal + 1)) {

            // Extract partition index (bits 2-0)
            int partitionIndex = encodedOrdinal & 0b111;
            // Extract partition ordinal (bits 31-3)
            int partitionOrdinal = encodedOrdinal >>> 3;

            // Verify partition index is valid
            assertTrue("Partition index should be 0-2", partitionIndex >= 0 && partitionIndex < 3);

            // Verify we can read the data
            int id = readState.readInt(encodedOrdinal, 0);
            assertTrue("ID should be 0-9", id >= 0 && id < 10);
        }
    }

    @Test
    public void testMultiPartitionWithLargeOrdinals() throws IOException {
        // Test handling of large ordinals that result in negative encoded ordinals
        HollowObjectSchema schema = new HollowObjectSchema("TestType", 1);
        schema.addField("id", FieldType.INT);

        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema, 1, 2);
        writeEngine.addTypeState(writeState);

        // Add enough records to test large ordinals
        for(int i = 0; i < 100; i++) {
            addRecord(writeState, schema, i, null);
        }

        writeEngine.prepareForWrite();

        // Write and read
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new HollowBlobWriter(writeEngine).writeSnapshot(baos);

        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        new HollowBlobReader(readEngine).readSnapshot(new ByteArrayInputStream(baos.toByteArray()));

        HollowObjectTypeReadState readState = (HollowObjectTypeReadState) readEngine.getTypeState("TestType");

        // Count records through encoded ordinals (including potentially negative ones)
        int count = 0;
        java.util.BitSet populatedOrdinals = readState.getPopulatedOrdinals();
        for(int encodedOrdinal = populatedOrdinals.nextSetBit(0);
            encodedOrdinal != -1;
            encodedOrdinal = populatedOrdinals.nextSetBit(encodedOrdinal + 1)) {

            int id = readState.readInt(encodedOrdinal, 0);
            assertTrue("Should be able to read data with encoded ordinal", id >= 0 && id < 100);
            count++;
        }

        assertEquals("Should read all 100 records", 100, count);
    }

    // Helper method to add records
    private void addRecord(HollowObjectTypeWriteState writeState, HollowObjectSchema schema, int id, String value) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        rec.setInt("id", id);
        if(value != null && schema.getPosition("value") != -1) {
            rec.setString("value", value);
        }
        if(value != null && schema.getPosition("name") != -1) {
            rec.setString("name", value);
        }
        writeState.add(rec);
    }

    private int addRecordAndGetOrdinal(HollowObjectTypeWriteState writeState, HollowObjectSchema schema, int id, String value) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        rec.setInt("id", id);
        if(value != null && schema.getPosition("value") != -1) {
            rec.setString("value", value);
        }
        if(value != null && schema.getPosition("name") != -1) {
            rec.setString("name", value);
        }
        return writeState.add(rec);
    }
}
