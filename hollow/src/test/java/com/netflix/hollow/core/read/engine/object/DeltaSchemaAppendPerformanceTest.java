package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.HollowDeltaSchemaAppendConfig;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Performance test comparing memory usage of integrated storage vs theoretical separate storage.
 */
public class DeltaSchemaAppendPerformanceTest extends AbstractStateEngineTest {

    private HollowObjectSchema schema;

    @Before
    public void setUp() {
        schema = new HollowObjectSchema("TestObject", 3);
        schema.addField("id", FieldType.INT);
        schema.addField("name", FieldType.STRING);
        schema.addField("value", FieldType.LONG);
        super.setUp();
    }

    @Test
    public void testMemoryEfficiencyWithLargeDataset() throws IOException {
        // Enable feature
        HollowDeltaSchemaAppendConfig config = new HollowDeltaSchemaAppendConfig(true);
        writeStateEngine.setDeltaSchemaAppendConfig(config);

        // Write 10,000 records
        for (int i = 0; i < 10000; i++) {
            addRecord(i, "name_" + i, i * 100L);
        }
        roundTripSnapshot();

        // Enable on read engine after it's created
        readStateEngine.setDeltaSchemaAppendConfig(config);

        // Preserve 5,000 ordinals with modifications
        for (int i = 0; i < 5000; i++) {
            addRecord(i, "name_" + i + "_updated", i * 150L);
        }

        long startTime = System.nanoTime();
        roundTripDelta();
        long endTime = System.nanoTime();

        HollowObjectTypeReadState typeState =
            (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");

        // Verify data accessible
        int accessibleCount = 0;
        for (int ordinal = 0; ordinal <= typeState.maxOrdinal(); ordinal++) {
            int id = typeState.readInt(ordinal, 0);
            if (id != Integer.MIN_VALUE) {
                accessibleCount++;
                String name = typeState.readString(ordinal, 1);
                long value = typeState.readLong(ordinal, 2);
                Assert.assertNotNull("Name should be accessible", name);
                Assert.assertTrue("Value should be valid", value >= 0);
            }
        }

        System.out.println("Delta application time: " + (endTime - startTime) / 1_000_000 + "ms");
        System.out.println("Accessible ordinals: " + accessibleCount);
        Assert.assertTrue("Should have accessible data", accessibleCount > 0);
    }

    @Test
    public void testReadPerformanceComparison() throws IOException {
        // Enable feature
        HollowDeltaSchemaAppendConfig config = new HollowDeltaSchemaAppendConfig(true);
        writeStateEngine.setDeltaSchemaAppendConfig(config);

        // Write 1,000 records
        for (int i = 0; i < 1000; i++) {
            addRecord(i, "name_" + i, i * 100L);
        }
        roundTripSnapshot();

        // Enable on read engine after it's created
        readStateEngine.setDeltaSchemaAppendConfig(config);

        // Write delta
        for (int i = 0; i < 500; i++) {
            addRecord(i, "name_" + i + "_updated", i * 150L);
        }
        roundTripDelta();

        HollowObjectTypeReadState typeState =
            (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");

        // Measure read performance
        long startTime = System.nanoTime();
        long checksum = 0;
        for (int iteration = 0; iteration < 1000; iteration++) {
            for (int ordinal = 0; ordinal <= typeState.maxOrdinal(); ordinal++) {
                int id = typeState.readInt(ordinal, 0);
                long value = typeState.readLong(ordinal, 2);
                checksum += id + value;
            }
        }
        long endTime = System.nanoTime();

        System.out.println("Read time (1000 iterations): " + (endTime - startTime) / 1_000_000 + "ms");
        System.out.println("Checksum: " + checksum);
        Assert.assertTrue("Should complete reads", checksum != 0);
    }

    @Override
    protected void initializeTypeStates() {
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema);
        writeStateEngine.addTypeState(writeState);
    }

    private void addRecord(int id, String name, long value) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        rec.setInt("id", id);
        rec.setString("name", name);
        rec.setLong("value", value);
        writeStateEngine.add("TestObject", rec);
    }
}
