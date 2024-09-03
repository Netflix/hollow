package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import org.junit.Before;

public class HollowTypeDataElementsSplitJoinTest extends AbstractStateEngineTest {
    protected HollowObjectSchema schema;

    @Before
    public void setUp() {
        schema = new HollowObjectSchema("TestObject", 4);
        schema.addField("longField", HollowObjectSchema.FieldType.LONG);
        schema.addField("stringField", HollowObjectSchema.FieldType.STRING);
        schema.addField("intField", HollowObjectSchema.FieldType.INT);
        schema.addField("doubleField", HollowObjectSchema.FieldType.DOUBLE);

        super.setUp();
    }

    @Override
    protected void initializeTypeStates() {
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(schema));
    }

    protected void populateWriteStateEngine(int numRecords) {
        initWriteStateEngine();
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        for(int i=0;i<numRecords;i++) {
            rec.reset();
            rec.setLong("longField", i);
            rec.setString("stringField", "Value" + i);
            rec.setInt("intField", i);
            rec.setDouble("doubleField", i);

            writeStateEngine.add("TestObject", rec);
        }
    }

    protected void populateWriteStateEngine(int[] recordIds) {
        initWriteStateEngine();
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        for(int recordId : recordIds) {
            rec.reset();
            rec.setLong("longField", recordId);
            rec.setString("stringField", "Value" + recordId);
            rec.setInt("intField", recordId);
            rec.setDouble("doubleField", recordId);

            writeStateEngine.add("TestObject", rec);
        }
    }
}
