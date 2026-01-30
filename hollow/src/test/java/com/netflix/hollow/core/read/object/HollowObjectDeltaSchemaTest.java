package com.netflix.hollow.core.read.object;

import com.netflix.hollow.api.objects.delegate.HollowObjectGenericDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowDeltaSchemaAppendConfig;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowObjectDeltaSchemaTest extends AbstractStateEngineTest {

    HollowObjectSchema schema;
    HollowObjectSchema evolvedSchema;
    byte[] snapshotBlob;
    byte[] deltaBlob;

    @Before
    public void setUp() {
        schema = new HollowObjectSchema("TestObject", 2);
        schema.addField("f1", HollowObjectSchema.FieldType.INT);
        schema.addField("f2", HollowObjectSchema.FieldType.STRING);

        evolvedSchema = new HollowObjectSchema("TestObject", 3);
        evolvedSchema.addField("f1", HollowObjectSchema.FieldType.INT);
        evolvedSchema.addField("f2", HollowObjectSchema.FieldType.STRING);
        evolvedSchema.addField("f3", HollowObjectSchema.FieldType.LONG);

        super.setUp();
    }

    @Test
    public void testSchemaInDelta() throws IOException {
        // Enable schema-in-delta feature with includeSchemaDefinitions=true
        writeStateEngine.setDeltaSchemaAppendConfig(new HollowDeltaSchemaAppendConfig(true, true));

        // Producer writes snapshot with 2-field schema
        addRecord(1, "one");
        addRecord(2, "two");
        addRecord(3, "three");
        snapshotBlob = writeSnapshot();

        // Consumer: Reads initial snapshot with old 2-field schema
        HollowReadStateEngine consumer = new HollowReadStateEngine();
        consumer.setDeltaSchemaAppendConfig(new HollowDeltaSchemaAppendConfig(true, true));
        readSnapshotBlob(consumer, snapshotBlob);

        // Producer evolves schema and writes delta with 3-field schema
        reinitializeTypeStates(consumer);
        addRecord(1, "one", 1L);
        addRecord(3, "three", 3L);
        deltaBlob = writeDelta();

        // Consumer applies first delta with schema evolution
        applyDeltaBlob(consumer, deltaBlob);
        HollowObjectTypeReadState state = (HollowObjectTypeReadState) consumer.getTypeState("TestObject");

        // Verify consumer schema was updated to 3 fields
        Assert.assertEquals("Schema should have 3 fields after schema-in-delta update",
                3, state.getSchema().numFields());

        // Verify first delta worked correctly - check populated ordinals
        java.util.BitSet populated1 = state.getPopulatedOrdinals();
        Assert.assertEquals("Should have ordinal 0", true, populated1.get(0));
        Assert.assertEquals("Should NOT have ordinal 1 (removed)", false, populated1.get(1));
        Assert.assertEquals("Should have ordinal 2", true, populated1.get(2));

        // Verify data at populated ordinals after first delta
        assertObject(state, 0, 1, "one", 1L);
        assertObject(state, 2, 3, "three", 3L);
    }

    @Test
    public void test() throws IOException {
        writeStateEngine.setDeltaSchemaAppendConfig(new HollowDeltaSchemaAppendConfig(true));

        // Producer writes snapshot with 2-field schema
        addRecord(1, "one");
        addRecord(2, "two");
        addRecord(3, "three");
        snapshotBlob = writeSnapshot();

        // Consumer1: Reads initial snapshot with old 2-field schema
        HollowReadStateEngine consumer1 = new HollowReadStateEngine();
        consumer1.setDeltaSchemaAppendConfig(new HollowDeltaSchemaAppendConfig(true));
        readSnapshotBlob(consumer1, snapshotBlob);

        // Producer evolves schema and writes delta with 3-field schema
        reinitializeTypeStates(consumer1);
        addRecord(1, "one", 1L);
        addRecord(3, "three", 3L);
        addRecord(10000, "ten thousand", 10000L);
        addRecord(0, "zero", 0L);
        deltaBlob = writeDelta();

        // Consumer1 applies delta but has old 2-field schema - can only read f1, f2
        applyDeltaBlob(consumer1, deltaBlob);
        HollowObjectTypeReadState oldConsumerState = (HollowObjectTypeReadState) consumer1.getTypeState("TestObject");
        Assert.assertEquals(4, oldConsumerState.maxOrdinal());
        assertObject(oldConsumerState, 0, 1, "one");
        assertObject(oldConsumerState, 2, 3, "three");

        // Producer writes snapshot with evolved schema
        writeStateEngine.prepareForNextCycle();
        addRecord(1, "one", 1L);
        addRecord(3, "three", 3L);
        addRecord(10000, "ten thousand", 10000L);
        addRecord(0, "zero", 0L);
        byte[] evolvedSnapshotBlob = writeSnapshot();

        // Consumer2: Reads evolved snapshot with 3-field schema
        HollowReadStateEngine consumer2 = new HollowReadStateEngine();
        consumer2.setDeltaSchemaAppendConfig(new HollowDeltaSchemaAppendConfig(true));
        readSnapshotBlob(consumer2, evolvedSnapshotBlob);
        HollowObjectTypeReadState newConsumerState = (HollowObjectTypeReadState) consumer2.getTypeState("TestObject");
        assertObject(newConsumerState, 0, 1, "one", 1L);
        assertObject(newConsumerState, 2, 3, "three", 3L);
        assertObject(newConsumerState, 3, 10000, "ten thousand", 10000L);
        assertObject(newConsumerState, 4, 0, "zero", 0L);

        // Verify consumer1 still only sees old schema fields
        assertObject(oldConsumerState, 0, 1, "one");
        assertObject(oldConsumerState, 2, 3, "three");
    }

    private void addRecord(int intVal, String strVal) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        rec.setInt("f1", intVal);
        rec.setString("f2", strVal);
        writeStateEngine.add("TestObject", rec);
    }

    private void addRecord(int intVal, String strVal, long longVal) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(evolvedSchema);
        rec.setInt("f1", intVal);
        rec.setString("f2", strVal);
        rec.setLong("f3", longVal);
        writeStateEngine.add("TestObject", rec);
    }

    private void assertObject(HollowObjectTypeReadState readState, int ordinal, int intVal, String strVal) {
        GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(readState), ordinal);
        Assert.assertEquals(intVal, obj.getInt("f1"));
        Assert.assertEquals(strVal, obj.getString("f2"));
    }

    private void assertObject(HollowObjectTypeReadState readState, int ordinal, int intVal, String strVal, long longVal) {
        GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(readState), ordinal);
        Assert.assertEquals(intVal, obj.getInt("f1"));
        Assert.assertEquals(strVal, obj.getString("f2"));
        Assert.assertEquals(longVal, obj.getLong("f3"));
    }

    private void assertObject(HollowObjectTypeReadState readState, int ordinal, int intVal, String strVal, Long longVal) {
        GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(readState), ordinal);
        Assert.assertEquals(intVal, obj.getInt("f1"));
        Assert.assertEquals(strVal, obj.getString("f2"));
        if (longVal == null) {
            Assert.assertTrue(obj.isNull("f3"));
        } else {
            Assert.assertEquals(longVal.longValue(), obj.getLong("f3"));
        }
    }

    @Override
    protected void initializeTypeStates() {
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema);
        writeStateEngine.addTypeState(writeState);
    }

    private void reinitializeTypeStates(HollowReadStateEngine readEngine) {
        HollowWriteStateEngine newEngine = new HollowWriteStateEngine();
        newEngine.setDeltaSchemaAppendConfig(writeStateEngine.getDeltaSchemaAppendConfig());
        newEngine.addTypeState(new HollowObjectTypeWriteState(evolvedSchema));
        newEngine.restoreFrom(readEngine);
        writeStateEngine = newEngine;
    }

    private byte[] writeSnapshot() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new HollowBlobWriter(writeStateEngine).writeSnapshot(baos);
        writeStateEngine.prepareForNextCycle();
        return baos.toByteArray();
    }

    private byte[] writeDelta() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new HollowBlobWriter(writeStateEngine).writeDelta(baos);
        return baos.toByteArray();
    }

    private void readSnapshotBlob(HollowReadStateEngine consumer, byte[] blob) throws IOException {
        HollowBlobReader reader = new HollowBlobReader(consumer);
        try (HollowBlobInput in = HollowBlobInput.serial(blob)) {
            reader.readSnapshot(in);
        }
    }

    private void applyDeltaBlob(HollowReadStateEngine consumer, byte[] blob) throws IOException {
        HollowBlobReader reader = new HollowBlobReader(consumer);
        try (HollowBlobInput in = HollowBlobInput.serial(blob)) {
            reader.applyDelta(in);
        }
    }
}