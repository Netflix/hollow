package com.netflix.hollow.core.write;

import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import org.junit.Assert;
import org.junit.Test;

public class DeltaSchemaAppendDataCollectorTest {

    @Test
    public void testDetectsSchemaEvolution() {
        // Setup: Create write state with initial schema
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowObjectSchema oldSchema = new HollowObjectSchema("TestType", 2);
        oldSchema.addField("id", FieldType.INT);
        oldSchema.addField("name", FieldType.STRING);

        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(oldSchema);
        writeStateEngine.addTypeState(writeState);

        // Add a record and prepare for next cycle
        HollowObjectWriteRecord rec1 = new HollowObjectWriteRecord(oldSchema);
        rec1.setInt("id", 1);
        rec1.setString("name", "Alice");
        writeStateEngine.add("TestType", rec1);
        writeStateEngine.prepareForNextCycle();

        // Simulate schema evolution: create new schema with added field
        HollowObjectSchema newSchema = new HollowObjectSchema("TestType", 3);
        newSchema.addField("id", FieldType.INT);
        newSchema.addField("name", FieldType.STRING);
        newSchema.addField("email", FieldType.STRING);

        // Replace write state with evolved schema
        HollowWriteStateEngine newWriteStateEngine = new HollowWriteStateEngine();
        HollowDeltaSchemaAppendConfig config = new HollowDeltaSchemaAppendConfig(true);
        newWriteStateEngine.setDeltaSchemaAppendConfig(config);

        HollowObjectTypeWriteState newWriteState = new HollowObjectTypeWriteState(newSchema);
        newWriteStateEngine.addTypeState(newWriteState);

        // Restore from previous state (simulates restoreFrom)
        // For this test, we'll manually set the previous schema on the collector

        HollowObjectWriteRecord rec2 = new HollowObjectWriteRecord(newSchema);
        rec2.setInt("id", 1);
        rec2.setString("name", "Alice");
        rec2.setString("email", "alice@example.com");
        newWriteStateEngine.add("TestType", rec2);

        // Create collector and check if it detects schema evolution
        DeltaSchemaAppendDataCollector collector = new DeltaSchemaAppendDataCollector(newWriteStateEngine);

        // Manually set previous schema for testing
        collector.setPreviousSchema("TestType", oldSchema);

        boolean hasSchemaEvolved = collector.hasSchemaEvolved("TestType");
        Assert.assertTrue("Should detect schema evolution", hasSchemaEvolved);
    }

    @Test
    public void testNoSchemaEvolutionWhenSchemasMatch() {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowObjectSchema schema = new HollowObjectSchema("TestType", 2);
        schema.addField("id", FieldType.INT);
        schema.addField("name", FieldType.STRING);

        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema);
        writeStateEngine.addTypeState(writeState);

        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        rec.setInt("id", 1);
        rec.setString("name", "Alice");
        writeStateEngine.add("TestType", rec);

        DeltaSchemaAppendDataCollector collector = new DeltaSchemaAppendDataCollector(writeStateEngine);
        collector.setPreviousSchema("TestType", schema);

        boolean hasSchemaEvolved = collector.hasSchemaEvolved("TestType");
        Assert.assertFalse("Should not detect schema evolution for identical schemas", hasSchemaEvolved);
    }
}
