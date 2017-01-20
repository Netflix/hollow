package com.netflix.hollow.core.read.object;

import java.util.BitSet;

import org.junit.Assert;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import java.io.IOException;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import org.junit.Test;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import org.junit.Before;
import com.netflix.hollow.core.AbstractStateEngineTest;

public class HollowObjectShardedTest extends AbstractStateEngineTest {

    HollowObjectSchema schema;

    @Before
    public void setUp() {
        schema = new HollowObjectSchema("TestObject", 3);
        schema.addField("longField", FieldType.LONG);
        schema.addField("intField", FieldType.INT);
        schema.addField("doubleField", FieldType.DOUBLE);

        super.setUp();
    }
    
    @Test
    public void testShardedData() throws IOException {
    
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        
        for(int i=0;i<1000;i++) {
            rec.reset();
            rec.setLong("longField", i);
            rec.setInt("intField", i);
            rec.setDouble("doubleField", i);
            
            writeStateEngine.add("TestObject", rec);
        }
        
        roundTripSnapshot();
        
        Assert.assertEquals(4, readStateEngine.getTypeState("TestObject").numShards());
        
        for(int i=0;i<1000;i++) {
            GenericHollowObject obj = new GenericHollowObject(readStateEngine, "TestObject", i); 
            
            Assert.assertEquals(i, obj.getLong("longField"));
            Assert.assertEquals(i, obj.getInt("intField"));
            Assert.assertEquals((double)i, obj.getDouble("doubleField"), 0);
        }

        for(int i=0;i<1000;i++) {
            rec.reset();
            rec.setLong("longField", i*2);
            rec.setInt("intField", i*2);
            rec.setDouble("doubleField", i*2);
            
            writeStateEngine.add("TestObject", rec);
        }
        
        roundTripDelta();
        
        int expectedValue = 0;
        
        BitSet populatedOrdinals = readStateEngine.getTypeState("TestObject").getPopulatedOrdinals();
        
        int ordinal = populatedOrdinals.nextSetBit(0);
        while(ordinal != -1) {
            GenericHollowObject obj = new GenericHollowObject(readStateEngine, "TestObject", ordinal);
            
            Assert.assertEquals(expectedValue, obj.getLong("longField"));
            Assert.assertEquals(expectedValue, obj.getInt("intField"));
            Assert.assertEquals(expectedValue, obj.getDouble("doubleField"), 0);
            
            expectedValue += 2;
            ordinal = populatedOrdinals.nextSetBit(ordinal+1);
        }
    }
    
    @Override
    protected void initializeTypeStates() {
        writeStateEngine.setTargetMaxTypeShardSize(4096);
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(schema));
    }


}
