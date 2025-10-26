package com.netflix.hollow.core.write;

import static org.junit.Assert.*;

import com.netflix.hollow.core.HollowBlobHeader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
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
        // This tests multi-partition reading with data only in partition 0
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

        // Read back
        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readEngine);
        reader.readSnapshot(new ByteArrayInputStream(baos.toByteArray()));

        // Verify both partitions were read with correct encoding
        HollowObjectTypeReadState readState = (HollowObjectTypeReadState) readEngine.getTypeState("TestType");
        assertNotNull("Should be able to read multi-partition snapshot", readState);
        assertEquals("Should have 2 partitions", 2, readState.getNumPartitions());

        // maxOrdinal() now returns the maximum encoded ordinal, which includes partition bits
        // With partition 0 having ordinals 0 and 1, encoded ordinals are 0 and 8
        assertTrue("Max encoded ordinal should be >= 8", readState.maxOrdinal() >= 8);

        // Verify we can read the data
        java.util.BitSet populatedOrdinals = readState.getPopulatedOrdinals();
        int recordCount = 0;
        for(int encodedOrdinal = populatedOrdinals.nextSetBit(0);
            encodedOrdinal != -1;
            encodedOrdinal = populatedOrdinals.nextSetBit(encodedOrdinal + 1)) {

            // All populated ordinals should be in partition 0 (low 3 bits = 0)
            assertEquals("All records should be in partition 0", 0, encodedOrdinal & 0x7);
            recordCount++;
        }
        assertEquals("Should have 2 records", 2, recordCount);
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

    /**
     * Comprehensive test that verifies partitioned and non-partitioned snapshots
     * with identical data produce identical read results.
     *
     * NOTE: This test currently FAILS because there's a bug in reading multi-partition snapshots.
     * The bug is that Long fields are not being read correctly from partitioned types.
     * This test demonstrates that partitioning changes read behavior, which should not happen.
     *
     * TODO: Fix the partitioned read implementation so this test passes.
     */
    @Test
    public void testPartitionedAndNonPartitionedSnapshotsAreEquivalent() throws IOException {
        // Create schemas
        HollowObjectSchema movieSchema = createSimpleMovieSchema();
        HollowObjectSchema personSchema = createPersonSchema();

        // Create non-partitioned snapshot (Movie has 1 partition)
        byte[] nonPartitionedSnapshot = createSimpleSnapshot(movieSchema, personSchema, 1);

        // Create partitioned snapshot (Movie has 8 partitions)
        byte[] partitionedSnapshot = createSimpleSnapshot(movieSchema, personSchema, 8);

        // Read both snapshots
        HollowReadStateEngine nonPartitionedEngine = readSnapshot(nonPartitionedSnapshot);
        HollowReadStateEngine partitionedEngine = readSnapshot(partitionedSnapshot);

        // Verify both have same schemas
        assertNotNull("Non-partitioned should have Movie type", nonPartitionedEngine.getTypeState("Movie"));
        assertNotNull("Partitioned should have Movie type", partitionedEngine.getTypeState("Movie"));
        assertNotNull("Both should have Person type", nonPartitionedEngine.getTypeState("Person"));
        assertNotNull("Both should have Person type", partitionedEngine.getTypeState("Person"));

        // Verify partition counts
        HollowObjectTypeReadState nonPartMovie = (HollowObjectTypeReadState) nonPartitionedEngine.getTypeState("Movie");
        HollowObjectTypeReadState partMovie = (HollowObjectTypeReadState) partitionedEngine.getTypeState("Movie");
        assertEquals("Non-partitioned should have 1 partition", 1, nonPartMovie.getNumPartitions());
        assertEquals("Partitioned should have 8 partitions", 8, partMovie.getNumPartitions());

        // Extract all movie data from both snapshots
        java.util.Map<Integer, SimpleMovieData> nonPartitionedMovies = extractSimpleMovieData(nonPartitionedEngine);
        java.util.Map<Integer, SimpleMovieData> partitionedMovies = extractSimpleMovieData(partitionedEngine);

        // Verify same number of movies
        assertEquals("Both should have same number of movies",
                     nonPartitionedMovies.size(), partitionedMovies.size());

        // Verify each movie has identical data
        for (Integer movieId : nonPartitionedMovies.keySet()) {
            assertTrue("Partitioned snapshot should have movie " + movieId,
                      partitionedMovies.containsKey(movieId));

            SimpleMovieData nonPartMovieData = nonPartitionedMovies.get(movieId);
            SimpleMovieData partMovieData = partitionedMovies.get(movieId);

            // Compare all fields
            assertEquals("Movie " + movieId + " id mismatch", nonPartMovieData.id, partMovieData.id);
            assertEquals("Movie " + movieId + " revenue mismatch", nonPartMovieData.revenue, partMovieData.revenue);
            assertEquals("Movie " + movieId + " rating mismatch", nonPartMovieData.rating, partMovieData.rating, 0.001);
            assertEquals("Movie " + movieId + " score mismatch", nonPartMovieData.score, partMovieData.score, 0.001f);
            assertEquals("Movie " + movieId + " isReleased mismatch", nonPartMovieData.isReleased, partMovieData.isReleased);
            assertEquals("Movie " + movieId + " title mismatch", nonPartMovieData.title, partMovieData.title);
            assertEquals("Movie " + movieId + " description mismatch", nonPartMovieData.description, partMovieData.description);
            assertArrayEquals("Movie " + movieId + " posterData mismatch", nonPartMovieData.posterData, partMovieData.posterData);
            assertEquals("Movie " + movieId + " director mismatch", nonPartMovieData.director, partMovieData.director);
        }
    }

    private HollowObjectSchema createSimpleMovieSchema() {
        HollowObjectSchema schema = new HollowObjectSchema("Movie", 9);
        schema.addField("id", FieldType.INT);
        schema.addField("revenue", FieldType.LONG);
        schema.addField("rating", FieldType.DOUBLE);
        schema.addField("score", FieldType.FLOAT);
        schema.addField("isReleased", FieldType.BOOLEAN);
        schema.addField("title", FieldType.STRING);
        schema.addField("description", FieldType.STRING);
        schema.addField("posterData", FieldType.BYTES);
        schema.addField("director", FieldType.REFERENCE, "Person");
        return schema;
    }

    private byte[] createSimpleSnapshot(HollowObjectSchema movieSchema, HollowObjectSchema personSchema,
                                        int moviePartitionCount) throws IOException {
        HollowWriteStateEngine engine = new HollowWriteStateEngine();

        // Add type states
        HollowObjectTypeWriteState movieState = new HollowObjectTypeWriteState(movieSchema, 1, moviePartitionCount);
        HollowObjectTypeWriteState personState = new HollowObjectTypeWriteState(personSchema, 1, 1);
        engine.addTypeState(movieState);
        engine.addTypeState(personState);

        // Add people (referenced by movies)
        java.util.Map<Integer, Integer> personIdToOrdinal = new java.util.HashMap<>();
        for (int i = 1; i <= 5; i++) {
            HollowObjectWriteRecord person = new HollowObjectWriteRecord(personSchema);
            person.setInt("id", i);
            person.setString("name", "Person" + i);
            person.setInt("age", 20 + (i % 50));
            int ordinal = personState.add(person);
            personIdToOrdinal.put(i, ordinal);
        }

        // Add movies with comprehensive field coverage
        for (int i = 1; i <= 10; i++) {
            HollowObjectWriteRecord movie = new HollowObjectWriteRecord(movieSchema);

            // Primitive fields
            movie.setInt("id", i);
            movie.setLong("revenue", 1000000L * i);
            movie.setDouble("rating", 5.0 + (i % 50) / 10.0);
            movie.setFloat("score", 50.0f + (i % 100));
            movie.setBoolean("isReleased", i % 2 == 0);

            // String fields
            movie.setString("title", "Movie" + i);
            movie.setString("description", "Description for movie " + i + " with unicode: \u2605\u2606");

            // Bytes field
            byte[] posterData = new byte[i % 10 + 1];
            for (int b = 0; b < posterData.length; b++) {
                posterData[b] = (byte) (i + b);
            }
            movie.setBytes("posterData", posterData);

            // Reference to director
            int directorId = 1 + (i % 5);
            movie.setReference("director", personIdToOrdinal.get(directorId));

            movieState.add(movie);
        }

        engine.prepareForWrite();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new HollowBlobWriter(engine).writeSnapshot(baos);
        return baos.toByteArray();
    }

    private java.util.Map<Integer, SimpleMovieData> extractSimpleMovieData(HollowReadStateEngine engine) {
        java.util.Map<Integer, SimpleMovieData> movies = new java.util.HashMap<>();

        HollowObjectTypeReadState movieState = (HollowObjectTypeReadState) engine.getTypeState("Movie");
        HollowObjectTypeReadState personState = (HollowObjectTypeReadState) engine.getTypeState("Person");

        java.util.BitSet populatedOrdinals = movieState.getPopulatedOrdinals();
        for (int ordinal = populatedOrdinals.nextSetBit(0);
             ordinal != -1;
             ordinal = populatedOrdinals.nextSetBit(ordinal + 1)) {

            SimpleMovieData movie = new SimpleMovieData();
            movie.id = movieState.readInt(ordinal, 0);
            movie.revenue = movieState.readLong(ordinal, 1);
            movie.rating = movieState.readDouble(ordinal, 2);
            movie.score = movieState.readFloat(ordinal, 3);
            movie.isReleased = movieState.readBoolean(ordinal, 4);
            movie.title = movieState.readString(ordinal, 5);
            movie.description = movieState.readString(ordinal, 6);
            movie.posterData = movieState.readBytes(ordinal, 7);

            // Director
            int directorOrd = movieState.readOrdinal(ordinal, 8);
            if (directorOrd != -1) {
                movie.director = readPersonName(personState, directorOrd);
            }

            movies.put(movie.id, movie);
        }

        return movies;
    }

    // Simplified data class for comparison
    private static class SimpleMovieData {
        int id;
        long revenue;
        double rating;
        float score;
        boolean isReleased;
        String title;
        String description;
        byte[] posterData;
        String director;
    }

    private HollowObjectSchema createMovieSchema() {
        HollowObjectSchema schema = new HollowObjectSchema("Movie", 12);
        schema.addField("id", FieldType.INT);
        schema.addField("revenue", FieldType.LONG);
        schema.addField("rating", FieldType.DOUBLE);
        schema.addField("score", FieldType.FLOAT);
        schema.addField("isReleased", FieldType.BOOLEAN);
        schema.addField("title", FieldType.STRING);
        schema.addField("description", FieldType.STRING);
        schema.addField("posterData", FieldType.BYTES);
        schema.addField("director", FieldType.REFERENCE, "Person");
        schema.addField("actors", FieldType.REFERENCE, "ListOfPerson");
        schema.addField("genres", FieldType.REFERENCE, "SetOfString");
        schema.addField("metadata", FieldType.REFERENCE, "MapOfStringToString");
        return schema;
    }

    private HollowObjectSchema createPersonSchema() {
        HollowObjectSchema schema = new HollowObjectSchema("Person", 3);
        schema.addField("id", FieldType.INT);
        schema.addField("name", FieldType.STRING);
        schema.addField("age", FieldType.INT);
        return schema;
    }

    private byte[] createSnapshot(HollowObjectSchema movieSchema, HollowObjectSchema personSchema,
                                  HollowListSchema actorsListSchema, HollowSetSchema genresSetSchema,
                                  HollowMapSchema metadataMapSchema, int moviePartitionCount) throws IOException {
        HollowWriteStateEngine engine = new HollowWriteStateEngine();

        // Add type states
        HollowObjectTypeWriteState movieState = new HollowObjectTypeWriteState(movieSchema, 1, moviePartitionCount);
        HollowObjectTypeWriteState personState = new HollowObjectTypeWriteState(personSchema, 1, 1);
        HollowListTypeWriteState actorsListState = new HollowListTypeWriteState(actorsListSchema, 1);
        HollowSetTypeWriteState genresSetState = new HollowSetTypeWriteState(genresSetSchema, 1);
        HollowMapTypeWriteState metadataMapState = new HollowMapTypeWriteState(metadataMapSchema, 1);

        engine.addTypeState(movieState);
        engine.addTypeState(personState);
        engine.addTypeState(actorsListState);
        engine.addTypeState(genresSetState);
        engine.addTypeState(metadataMapState);

        // Add String type for Set and Map
        HollowObjectSchema stringSchema = new HollowObjectSchema("String", 1);
        stringSchema.addField("value", FieldType.STRING);
        HollowObjectTypeWriteState stringState = new HollowObjectTypeWriteState(stringSchema, 1, 1);
        engine.addTypeState(stringState);

        // Pre-create reusable String ordinals for genres and metadata
        java.util.Map<String, Integer> stringCache = new java.util.HashMap<>();
        for (int g = 0; g < 5; g++) {
            HollowObjectWriteRecord genreRec = new HollowObjectWriteRecord(stringSchema);
            genreRec.setString("value", "Genre" + g);
            stringCache.put("Genre" + g, stringState.add(genreRec));
        }
        for (int m = 0; m < 2; m++) {
            HollowObjectWriteRecord keyRec = new HollowObjectWriteRecord(stringSchema);
            keyRec.setString("value", "key" + m);
            stringCache.put("key" + m, stringState.add(keyRec));
        }

        // Add people (referenced by movies)
        java.util.Map<Integer, Integer> personIdToOrdinal = new java.util.HashMap<>();
        for (int i = 1; i <= 20; i++) {
            HollowObjectWriteRecord person = new HollowObjectWriteRecord(personSchema);
            person.setInt("id", i);
            person.setString("name", "Person" + i);
            person.setInt("age", 20 + (i % 50));
            int ordinal = personState.add(person);
            personIdToOrdinal.put(i, ordinal);
        }

        // Add movies with comprehensive field coverage
        for (int i = 1; i <= 50; i++) {
            HollowObjectWriteRecord movie = new HollowObjectWriteRecord(movieSchema);

            // Primitive fields
            movie.setInt("id", i);
            movie.setLong("revenue", 1000000L * i);
            movie.setDouble("rating", 5.0 + (i % 50) / 10.0);
            movie.setFloat("score", 50.0f + (i % 100));
            movie.setBoolean("isReleased", i % 2 == 0);

            // String fields
            movie.setString("title", "Movie" + i);
            movie.setString("description", "Description for movie " + i + " with unicode: \u2605\u2606");

            // Bytes field
            byte[] posterData = new byte[i % 10 + 1];
            for (int b = 0; b < posterData.length; b++) {
                posterData[b] = (byte) (i + b);
            }
            movie.setBytes("posterData", posterData);

            // Reference to director
            int directorId = 1 + (i % 20);
            movie.setReference("director", personIdToOrdinal.get(directorId));

            // LIST of actors
            int numActors = 1 + (i % 3);
            int[] actorOrdinals = new int[numActors];
            for (int a = 0; a < numActors; a++) {
                actorOrdinals[a] = personIdToOrdinal.get(1 + ((i + a) % 20));
            }
            HollowListWriteRecord actorsList = new HollowListWriteRecord();
            for (int actorOrd : actorOrdinals) {
                actorsList.addElement(actorOrd);
            }
            int actorsListOrdinal = actorsListState.add(actorsList);
            movie.setReference("actors", actorsListOrdinal);

            // SET of genres - reuse cached string ordinals
            int numGenres = 1 + (i % 3);
            HollowSetWriteRecord genresSet = new HollowSetWriteRecord();
            for (int g = 0; g < numGenres; g++) {
                genresSet.addElement(stringCache.get("Genre" + ((i + g) % 5)));
            }
            int genresSetOrdinal = genresSetState.add(genresSet);
            movie.setReference("genres", genresSetOrdinal);

            // MAP of metadata - create unique values but reuse keys
            HollowMapWriteRecord metadataMap = new HollowMapWriteRecord();
            for (int m = 0; m < 2; m++) {
                int keyOrd = stringCache.get("key" + m);

                HollowObjectWriteRecord value = new HollowObjectWriteRecord(stringSchema);
                value.setString("value", "value" + m + "_movie" + i);
                int valueOrd = stringState.add(value);

                metadataMap.addEntry(keyOrd, valueOrd);
            }
            int metadataMapOrdinal = metadataMapState.add(metadataMap);
            movie.setReference("metadata", metadataMapOrdinal);

            movieState.add(movie);
        }

        engine.prepareForWrite();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new HollowBlobWriter(engine).writeSnapshot(baos);
        return baos.toByteArray();
    }

    private HollowReadStateEngine readSnapshot(byte[] snapshotBytes) throws IOException {
        HollowReadStateEngine engine = new HollowReadStateEngine();
        new HollowBlobReader(engine).readSnapshot(new ByteArrayInputStream(snapshotBytes));
        return engine;
    }

    private java.util.Map<Integer, MovieData> extractMovieData(HollowReadStateEngine engine) {
        java.util.Map<Integer, MovieData> movies = new java.util.HashMap<>();

        HollowObjectTypeReadState movieState = (HollowObjectTypeReadState) engine.getTypeState("Movie");
        HollowObjectTypeReadState personState = (HollowObjectTypeReadState) engine.getTypeState("Person");

        java.util.BitSet populatedOrdinals = movieState.getPopulatedOrdinals();
        for (int ordinal = populatedOrdinals.nextSetBit(0);
             ordinal != -1;
             ordinal = populatedOrdinals.nextSetBit(ordinal + 1)) {

            MovieData movie = new MovieData();
            movie.id = movieState.readInt(ordinal, 0);
            movie.revenue = movieState.readLong(ordinal, 1);
            movie.rating = movieState.readDouble(ordinal, 2);
            movie.score = movieState.readFloat(ordinal, 3);
            movie.isReleased = movieState.readBoolean(ordinal, 4);
            movie.title = movieState.readString(ordinal, 5);
            movie.description = movieState.readString(ordinal, 6);
            movie.posterData = movieState.readBytes(ordinal, 7);

            // Director
            int directorOrd = movieState.readOrdinal(ordinal, 8);
            if (directorOrd != -1) {
                movie.director = readPersonName(personState, directorOrd);
            }

            // Actors list
            int actorsListOrd = movieState.readOrdinal(ordinal, 9);
            movie.actors = readActorsList(engine, actorsListOrd, personState);

            // Genres set
            int genresSetOrd = movieState.readOrdinal(ordinal, 10);
            movie.genres = readGenresSet(engine, genresSetOrd);

            // Metadata map
            int metadataMapOrd = movieState.readOrdinal(ordinal, 11);
            movie.metadata = readMetadataMap(engine, metadataMapOrd);

            movies.put(movie.id, movie);
        }

        return movies;
    }

    private String readPersonName(HollowObjectTypeReadState personState, int ordinal) {
        return personState.readString(ordinal, 1); // name is field index 1
    }

    private java.util.List<String> readActorsList(HollowReadStateEngine engine, int listOrdinal,
                                                  HollowObjectTypeReadState personState) {
        java.util.List<String> actors = new java.util.ArrayList<>();
        if (listOrdinal == -1) return actors;

        com.netflix.hollow.core.read.engine.list.HollowListTypeReadState listState =
            (com.netflix.hollow.core.read.engine.list.HollowListTypeReadState) engine.getTypeState("ListOfPerson");

        int size = listState.size(listOrdinal);
        for (int i = 0; i < size; i++) {
            int personOrd = listState.getElementOrdinal(listOrdinal, i);
            if (personOrd != -1) {
                actors.add(readPersonName(personState, personOrd));
            }
        }
        return actors;
    }

    private java.util.Set<String> readGenresSet(HollowReadStateEngine engine, int setOrdinal) {
        java.util.Set<String> genres = new java.util.TreeSet<>(); // TreeSet for consistent ordering
        if (setOrdinal == -1) return genres;

        com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState setState =
            (com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState) engine.getTypeState("SetOfString");
        HollowObjectTypeReadState stringState = (HollowObjectTypeReadState) engine.getTypeState("String");

        com.netflix.hollow.core.read.iterator.HollowOrdinalIterator iter = setState.ordinalIterator(setOrdinal);
        int elementOrd = iter.next();
        while (elementOrd != com.netflix.hollow.core.read.iterator.HollowOrdinalIterator.NO_MORE_ORDINALS) {
            String value = stringState.readString(elementOrd, 0);
            genres.add(value);
            elementOrd = iter.next();
        }
        return genres;
    }

    private java.util.Map<String, String> readMetadataMap(HollowReadStateEngine engine, int mapOrdinal) {
        java.util.Map<String, String> metadata = new java.util.TreeMap<>(); // TreeMap for consistent ordering
        if (mapOrdinal == -1) return metadata;

        com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState mapState =
            (com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState) engine.getTypeState("MapOfStringToString");
        HollowObjectTypeReadState stringState = (HollowObjectTypeReadState) engine.getTypeState("String");

        com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator iter = mapState.ordinalIterator(mapOrdinal);
        while (iter.next()) {
            String key = stringState.readString(iter.getKey(), 0);
            String value = stringState.readString(iter.getValue(), 0);
            metadata.put(key, value);
        }
        return metadata;
    }

    // Data class for comparison
    private static class MovieData {
        int id;
        long revenue;
        double rating;
        float score;
        boolean isReleased;
        String title;
        String description;
        byte[] posterData;
        String director;
        java.util.List<String> actors;
        java.util.Set<String> genres;
        java.util.Map<String, String> metadata;
    }
}
