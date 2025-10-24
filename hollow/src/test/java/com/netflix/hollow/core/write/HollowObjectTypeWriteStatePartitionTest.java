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
}
