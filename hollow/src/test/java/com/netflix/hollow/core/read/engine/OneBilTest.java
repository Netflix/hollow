package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class OneBilTest extends AbstractStateEngineTest {
    private HollowObjectSchema schema;
    @Override
    protected void initializeTypeStates() {
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema);
        writeStateEngine.addTypeState(writeState);
    }

    @Before
    public void setUp() {
        schema = new HollowObjectSchema("test", 1);
        schema.addField("field", HollowObjectSchema.FieldType.INT);

        super.setUp();
    }

    @Test
    public void oneBilTest() throws IOException {
        roundTripSnapshot();
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        for(int j = 0; j < 2; j++) {
            for (int i = 0; i < 300_000_000; i++) {
                if (i % 1_000_000 == 0)
                    System.out.println(((float) i+(j*300_000_000)) / (600_000_000));
                rec.setInt("field", i+j*300_000_000);

                writeStateEngine.add("test", rec);
            }
            roundTripDelta();
        }
        System.out.println("Done round tripping");

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState("test");
        for(int i = 0; i < 600_000_000; i++) {
            int res = typeState.readInt(i, 0);
            Assert.assertEquals(i, res);
        }
    }
}
