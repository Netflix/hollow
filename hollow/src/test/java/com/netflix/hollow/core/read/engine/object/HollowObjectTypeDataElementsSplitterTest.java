package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.IOException;
import java.util.Random;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowObjectTypeDataElementsSplitterTest extends AbstractStateEngineTest  {
    HollowObjectSchema schema;

    @Before
    public void setUp() {
        schema = new HollowObjectSchema("TestObject", 2);
        schema.addField("longField", HollowObjectSchema.FieldType.LONG);
        schema.addField("stringField", HollowObjectSchema.FieldType.STRING);

        super.setUp();
    }

    @Override
    protected void initializeTypeStates() {
        writeStateEngine.setTargetMaxTypeShardSize(4096);
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(schema));
    }

    // @Test
    public void testSplits() throws IOException {
        HollowObjectTypeDataElementsSplitter splitter = new HollowObjectTypeDataElementsSplitter();

        HollowObjectTypeReadState typeReadState = populateTypeStateWith(5);
        splitter.split(typeReadState.currentDataElements()[0], 2);

        try {
            splitter.split(typeReadState.currentDataElements()[0], 3);
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected, numSplits should be a power of 2
        } catch (Exception e) {
            Assert.fail();
        }

        HollowObjectTypeDataElements[] result = splitter.split(typeReadState.currentDataElements()[0], 1);
        assertEquals(typeReadState.currentDataElements()[0], result[0]);    // SNAP: here

    }

    private HollowObjectTypeReadState populateTypeStateWith(int numRecords) throws IOException {
        initWriteStateEngine();
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        for(int i=0;i<numRecords;i++) {
            rec.reset();
            rec.setLong("longField", i);
            rec.setString("stringField", "Value" + i);

            writeStateEngine.add("TestObject", rec);
        }
        roundTripSnapshot();
        return (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");
    }

    private void assertDataUnchanged(int numRecords) {
        for(int i=0;i<numRecords;i++) {
            GenericHollowObject obj = new GenericHollowObject(readStateEngine, "TestObject", i);
            assertEquals(i, obj.getLong("longField"));
            assertEquals("Value"+i, obj.getString("stringField"));
        }
    }

    // SNAP: TODO: test a simple split, and input edge cases (complex split/join done elsewhere)

    // TODO: test maxOrdinal of splits should be initialized to -1
}
