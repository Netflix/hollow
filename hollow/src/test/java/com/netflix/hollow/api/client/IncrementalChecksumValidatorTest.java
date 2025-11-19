package com.netflix.hollow.api.client;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class IncrementalChecksumValidatorTest {

    @Test
    public void testIncrementalValidation_AllTypesMatch() throws Exception {
        // Setup state engine with multiple types
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        mapper.add(new TypeA("a1", 100));
        mapper.add(new TypeB("b1", 200));

        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        roundTrip(writeEngine, readEngine);

        // Compute per-type checksums
        HollowChecksum checksum = HollowChecksum.forStateEngine(readEngine);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("hollow.checksum.TypeA", String.valueOf(checksum.getTypeChecksum("TypeA")));
        metadata.put("hollow.checksum.TypeB", String.valueOf(checksum.getTypeChecksum("TypeB")));

        ChecksumValidator validator = new ChecksumValidator();
        ChecksumValidator.IncrementalResult result = validator.validateIncremental(
            readEngine, metadata, checksum);

        Assert.assertTrue(result.isValid());
        Assert.assertEquals(0, result.getMismatchedTypes().size());
    }

    @Test
    public void testIncrementalValidation_OneTypeMismatch() throws Exception {
        // Setup with intentional mismatch
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        mapper.add(new TypeA("a1", 100));
        mapper.add(new TypeB("b1", 200));

        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        roundTrip(writeEngine, readEngine);

        HollowChecksum checksum = HollowChecksum.forStateEngine(readEngine);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("hollow.checksum.TypeA", String.valueOf(checksum.getTypeChecksum("TypeA")));
        metadata.put("hollow.checksum.TypeB", "99999"); // Intentional mismatch

        ChecksumValidator validator = new ChecksumValidator();
        ChecksumValidator.IncrementalResult result = validator.validateIncremental(
            readEngine, metadata, checksum);

        Assert.assertFalse(result.isValid());
        Assert.assertEquals(1, result.getMismatchedTypes().size());
        Assert.assertTrue(result.getMismatchedTypes().contains("TypeB"));
    }

    @Test
    public void testIncrementalValidation_FallbackToFullChecksum() throws Exception {
        // If no per-type checksums in metadata, should fall back to full validation
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        mapper.add(new TypeA("a1", 100));

        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        roundTrip(writeEngine, readEngine);

        HollowChecksum checksum = HollowChecksum.forStateEngine(readEngine);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("hollow.checksum", String.valueOf(checksum.intValue()));

        ChecksumValidator validator = new ChecksumValidator();
        ChecksumValidator.IncrementalResult result = validator.validateIncremental(
            readEngine, metadata, checksum);

        Assert.assertTrue(result.isValid());
        Assert.assertEquals(0, result.getMismatchedTypes().size());
    }

    @Test
    public void testIncrementalValidation_NullMetadata() throws Exception {
        // Test that null metadata returns valid result
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        mapper.add(new TypeA("a1", 100));

        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        roundTrip(writeEngine, readEngine);

        HollowChecksum checksum = HollowChecksum.forStateEngine(readEngine);

        ChecksumValidator validator = new ChecksumValidator();
        ChecksumValidator.IncrementalResult result = validator.validateIncremental(
            readEngine, null, checksum);

        Assert.assertTrue(result.isValid());
        Assert.assertEquals(0, result.getMismatchedTypes().size());
    }

    @Test
    public void testIncrementalValidation_InvalidChecksumFormat() throws Exception {
        // Test that malformed checksum strings are handled gracefully
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        mapper.add(new TypeA("a1", 100));
        mapper.add(new TypeB("b1", 200));

        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        roundTrip(writeEngine, readEngine);

        HollowChecksum checksum = HollowChecksum.forStateEngine(readEngine);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("hollow.checksum.TypeA", String.valueOf(checksum.getTypeChecksum("TypeA")));
        metadata.put("hollow.checksum.TypeB", "not-a-number"); // Invalid format

        ChecksumValidator validator = new ChecksumValidator();
        ChecksumValidator.IncrementalResult result = validator.validateIncremental(
            readEngine, metadata, checksum);

        // Should still be valid because invalid formats are skipped
        Assert.assertTrue(result.isValid());
        Assert.assertEquals(0, result.getMismatchedTypes().size());
    }

    @Test
    public void testIncrementalValidation_MultipleTypeMismatches() throws Exception {
        // Test that multiple mismatches are all detected
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        mapper.add(new TypeA("a1", 100));
        mapper.add(new TypeB("b1", 200));

        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        roundTrip(writeEngine, readEngine);

        HollowChecksum checksum = HollowChecksum.forStateEngine(readEngine);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("hollow.checksum.TypeA", "11111"); // Intentional mismatch
        metadata.put("hollow.checksum.TypeB", "22222"); // Intentional mismatch

        ChecksumValidator validator = new ChecksumValidator();
        ChecksumValidator.IncrementalResult result = validator.validateIncremental(
            readEngine, metadata, checksum);

        Assert.assertFalse(result.isValid());
        Assert.assertEquals(2, result.getMismatchedTypes().size());
        Assert.assertTrue(result.getMismatchedTypes().contains("TypeA"));
        Assert.assertTrue(result.getMismatchedTypes().contains("TypeB"));
    }

    // Test data classes
    static class TypeA {
        String name;
        int value;

        TypeA(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    static class TypeB {
        String id;
        int count;

        TypeB(String id, int count) {
            this.id = id;
            this.count = count;
        }
    }

    private void roundTrip(HollowWriteStateEngine writeEngine, HollowReadStateEngine readEngine) throws Exception {
        // Helper to serialize write engine and load into read engine
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        com.netflix.hollow.core.write.HollowBlobWriter writer =
            new com.netflix.hollow.core.write.HollowBlobWriter(writeEngine);
        writer.writeSnapshot(baos);

        com.netflix.hollow.core.read.engine.HollowBlobReader reader =
            new com.netflix.hollow.core.read.engine.HollowBlobReader(readEngine);
        reader.readSnapshot(com.netflix.hollow.core.read.HollowBlobInput.serial(baos.toByteArray()));
    }
}
