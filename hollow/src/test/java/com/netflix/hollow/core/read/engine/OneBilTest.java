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
        int size = 500_000_000;
        for(int i = 0; i < size; i++) {
            if(i%1_000_000==0)
                System.out.println((float)i/(size*2));
            HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
            rec.setInt("field", i);

            writeStateEngine.add("test", rec);
        }
        System.out.println("Round tripping");
        roundTripSnapshot();
        System.out.println("Done round tripping");

        for(int i = 0; i < size; i++) {
            if(i%1_000_000==0)
                System.out.println((float)(i+size)/(size*2));
            HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState("test");
            int res = typeState.readInt(i, 0);
            Assert.assertEquals(res, i);
        }
    }
}
