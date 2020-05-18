/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.core.write.restore;

import com.netflix.hollow.api.objects.delegate.HollowObjectGenericDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RestoreWriteStateEngineChangingSchemaTest extends AbstractStateEngineTest {

    HollowObjectSchema typeASchema_state1;
    HollowObjectSchema typeASchema_state2;

    HollowObjectSchema typeBSchema;
    HollowObjectSchema typeCSchema;

    private boolean afterRestoreWithNewSchemas;

    @Before
    public void setUp() {
        typeASchema_state1 = new HollowObjectSchema("TypeA", 3);
        typeASchema_state1.addField("a1", FieldType.BOOLEAN);
        typeASchema_state1.addField("a2", FieldType.STRING);
        typeASchema_state1.addField("a3", FieldType.REFERENCE, "TypeB");

        typeBSchema = new HollowObjectSchema("TypeB", 2);
        typeBSchema.addField("b1", FieldType.LONG);
        typeBSchema.addField("b2", FieldType.FLOAT);

        typeASchema_state2 = new HollowObjectSchema("TypeA", 3);
        typeASchema_state2.addField("a2", FieldType.STRING);
        typeASchema_state2.addField("a1", FieldType.BOOLEAN);
        typeASchema_state2.addField("a4", FieldType.REFERENCE, "TypeC");

        typeCSchema = new HollowObjectSchema("TypeC", 1);
        typeCSchema.addField("c1", FieldType.STRING);

        super.setUp();
    }

    @Test
    public void test() throws IOException {
        addState1Record(true, "zero", 0, 0.1f);  // ordinals 0, 0
        addState1Record(false, "one", 1, 1.1f);  // ordinals 1, 1
        addState1Record(true, "two", 1, 1.1f);   // ordinals 2, 1

        roundTripSnapshot();

        addState1Record(true, "zero", 0, 0.1f);  // 0, 0
        addState1Record(false, "one", 1, 1.1f);  // 1, 1
        addState1Record(false, "two", 2, 2.2f);  // 3, 2

        roundTripDelta();
        afterRestoreWithNewSchemas = true;
        restoreWriteStateEngineFromReadStateEngine();

        addState2Record(false, "one", "c1");  // 1, 0
        addState2Record(false, "two", "c2");  // 3, 1
        addState2Record(true, "zero", "c0");  // 0, 2
        addState2Record(true, "wxyz", "c3");  // 2, 3

        roundTripDelta();

        assertTypeA(0, true, "zero");
        assertTypeA(1, false, "one");
        assertTypeA(3, false, "two");
        assertTypeA(2, true, "wxyz");

        addState2Record(false, "one", "c1");  // 1, 0
        addState2Record(false, "two", "c2");  // 3, 1
        addState2Record(true, "zero", "c0");  // 0, 2
        addState2Record(true, "wxyz", "c3"); // 2, 3

        roundTripSnapshot();

        assertTypeAWithTypeC(0, true, "zero", "c0");
        assertTypeAWithTypeC(1, false, "one", "c1");
        assertTypeAWithTypeC(3, false, "two", "c2");
        assertTypeAWithTypeC(2, true, "wxyz", "c3");
    }
    
    @Test
    public void doesNotAllowImproperlyInitializedRestoredStateToProduceDelta() throws IOException {
        addState1Record(true, "zero", 0, 0.1f);  // ordinals 0, 0
        addState1Record(false, "one", 1, 1.1f);  // ordinals 1, 1
        addState1Record(true, "two", 1, 1.1f);   // ordinals 2, 1

        roundTripSnapshot();

        addState1Record(true, "zero", 0, 0.1f);  // 0, 0
        addState1Record(false, "one", 1, 1.1f);  // 1, 1
        addState1Record(false, "two", 2, 2.2f);  // 3, 2

        roundTripDelta();
        afterRestoreWithNewSchemas = true;
        
        ////restore, but initialize type states *after* restore
        writeStateEngine = new HollowWriteStateEngine();
        writeStateEngine.restoreFrom(readStateEngine);
        initializeTypeStates();
        writeStateEngine.prepareForNextCycle();
        
        addState2Record(false, "one", "c1");  // 1, 0
        addState2Record(false, "two", "c2");  // 3, 1
        addState2Record(true, "zero", "c0");  // 0, 2
        addState2Record(true, "wxyz", "c3");  // 2, 3

        try {
            roundTripDelta();
            Assert.fail("Should have thrown Exception when attempting to produce a delta!");
        } catch(IllegalStateException expected) {
            Assert.assertTrue(expected.getMessage().contains("TypeA"));
        }


    }

    private void assertTypeA(int ordinal, boolean a1, String a2) {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState)readStateEngine.getTypeState("TypeA");

        GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(typeState), ordinal);

        Assert.assertEquals(a1, obj.getBoolean("a1"));
        Assert.assertEquals(a2, obj.getString("a2"));
    }

    private void assertTypeAWithTypeC(int ordinal, boolean a1, String a2, String c1) {
        HollowObjectTypeReadState typeAState = (HollowObjectTypeReadState)readStateEngine.getTypeState("TypeA");

        GenericHollowObject typeAObj = new GenericHollowObject(new HollowObjectGenericDelegate(typeAState), ordinal);

        Assert.assertEquals(a1, typeAObj.getBoolean("a1"));
        Assert.assertEquals(a2, typeAObj.getString("a2"));

        GenericHollowObject typeCObj = (GenericHollowObject) typeAObj.getReferencedGenericRecord("a4");

        Assert.assertEquals(c1, typeCObj.getString("c1"));
    }

    private void addState1Record(boolean a1, String a2, long b1, float b2) {
        HollowObjectWriteRecord bRec = new HollowObjectWriteRecord(typeBSchema);
        bRec.setLong("b1", b1);
        bRec.setFloat("b2", b2);

        int bOrdinal = writeStateEngine.add("TypeB", bRec);

        HollowObjectWriteRecord aRec = new HollowObjectWriteRecord(typeASchema_state1);
        aRec.setBoolean("a1", a1);
        aRec.setString("a2", a2);
        aRec.setReference("a3", bOrdinal);

        writeStateEngine.add("TypeA", aRec);
    }

    private void addState2Record(boolean a1, String a2, String c1) {
        HollowObjectWriteRecord cRec = new HollowObjectWriteRecord(typeCSchema);
        cRec.setString("c1", c1);

        int cOrdinal = writeStateEngine.add("TypeC", cRec);

        HollowObjectWriteRecord aRec = new HollowObjectWriteRecord(typeASchema_state2);
        aRec.setBoolean("a1", a1);
        aRec.setString("a2", a2);
        aRec.setReference("a4", cOrdinal);

        writeStateEngine.add("TypeA", aRec);
    }


    @Override
    protected void initializeTypeStates() {
        if(!afterRestoreWithNewSchemas) {
            writeStateEngine.addTypeState(new HollowObjectTypeWriteState(typeBSchema));
            writeStateEngine.addTypeState(new HollowObjectTypeWriteState(typeASchema_state1));
        } else {
            writeStateEngine.addTypeState(new HollowObjectTypeWriteState(typeCSchema));
            writeStateEngine.addTypeState(new HollowObjectTypeWriteState(typeASchema_state2));
        }
    }

}
