package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import java.io.IOException;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AbstractHollowObjectTypeDataElementsSplitJoinTest extends AbstractStateEngineTest {
    protected HollowObjectSchema schema;

    @Mock
    protected HollowObjectTypeReadState mockObjectTypeState;

    @Before
    public void setUp() {
        schema = new HollowObjectSchema("TestObject", 4);
        schema.addField("longField", HollowObjectSchema.FieldType.LONG);
        schema.addField("stringField", HollowObjectSchema.FieldType.STRING);
        schema.addField("intField", HollowObjectSchema.FieldType.INT);
        schema.addField("doubleField", HollowObjectSchema.FieldType.DOUBLE);

        MockitoAnnotations.initMocks(this);
        HollowObjectTypeDataElements[] fakeDataElements = new HollowObjectTypeDataElements[5];
        when(mockObjectTypeState.currentDataElements()).thenReturn(fakeDataElements);
        super.setUp();
    }

    @Override
    protected void initializeTypeStates() {
        writeStateEngine.setTargetMaxTypeShardSize(4096);
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(schema));
    }

    private void populateWriteStateEngine(int numRecords) {
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

    private void populateWriteStateEngine(int[] recordIds) {
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

    protected HollowObjectTypeReadState populateTypeStateWith(int numRecords) throws IOException {
        populateWriteStateEngine(numRecords);
        roundTripSnapshot();
        return (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");
    }

    protected HollowObjectTypeReadState populateTypeStateWith(int[] recordIds) throws IOException {
        populateWriteStateEngine(recordIds);
        roundTripSnapshot();
        return (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");
    }

    protected HollowObjectTypeReadState populateTypeStateWithFilter(int numRecords) throws IOException {
        populateWriteStateEngine(numRecords);
        readStateEngine = new HollowReadStateEngine();
        HollowFilterConfig readFilter = new HollowFilterConfig(true);
        readFilter.addField("TestObject", "intField");
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine, readFilter);
        return (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");
    }

    protected void assertDataUnchanged(int numRecords) {
        assertDataUnchanged((HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject"), numRecords);
    }

    protected void assertDataUnchanged(HollowObjectTypeReadState typeState, int numRecords) {
        for(int i=0;i<numRecords;i++) {
            GenericHollowObject obj = new GenericHollowObject(typeState, i);
            assertEquals(i, obj.getLong("longField"));
            assertEquals("Value"+i, obj.getString("stringField"));
            assertEquals((double)i, obj.getDouble("doubleField"), 0);
            if (typeState.getSchema().numFields() == 4) {   // filtered
                assertEquals(i, obj.getInt("intField"));
            }
        }
    }
}
