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
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RestoreWriteStateEngineFieldRemovalCollapsesOrdinals extends AbstractStateEngineTest {

    HollowObjectSchema typeASchema;

    HollowObjectSchema typeBSchema_state1;
    HollowObjectSchema typeBSchema_state2;

    private boolean afterRestoreWithNewSchemas;

    @Before
    public void setUp() {
        typeASchema = new HollowObjectSchema("TypeA", 2);
        typeASchema.addField("a1", FieldType.INT);
        typeASchema.addField("a2", FieldType.REFERENCE, "TypeB");

        typeBSchema_state1 = new HollowObjectSchema("TypeB", 2);
        typeBSchema_state1.addField("b1", FieldType.INT);
        typeBSchema_state1.addField("b2", FieldType.BOOLEAN);

        typeBSchema_state2 = new HollowObjectSchema("TypeB", 1);
        typeBSchema_state2.addField("b1", FieldType.INT);

        super.setUp();
    }

    @Test
    public void test() throws IOException {
        addState1Record(0, 0, true);
        addState1Record(1, 1, true);
        addState1Record(2, 1, false);
        addState1Record(1, 1, false);

        roundTripSnapshot();
        afterRestoreWithNewSchemas = true;
        restoreWriteStateEngineFromReadStateEngine();

        addState2Record(1, 1);
        addState2Record(2, 1);
        addState2Record(0, 0);
        addState2Record(1, 1);
        addState2Record(1, 3);

        roundTripDelta();

        assertTypeA(0, 0, 0);
        assertTypeA(1, 1, 1);
        assertTypeA(4, 2, 1);
        assertTypeA(5, 1, 3);

        assertTypeB(0, 0);
        assertTypeB(1, 1);
        assertTypeB(3, 3);

        /// the following are removed objects
        Assert.assertFalse((readStateEngine.getTypeState("TypeA").getListener(PopulatedOrdinalListener.class)).getPopulatedOrdinals().get(2));
        Assert.assertFalse((readStateEngine.getTypeState("TypeB").getListener(PopulatedOrdinalListener.class)).getPopulatedOrdinals().get(2));

        assertTypeB(2, 1); /// this object was removed, because it was a duplicate with a lower ordinal.

        assertTypeA(2, 2, 2); /// these objects were removed and re-added, because they were referencing the removed duplicate record.
        assertTypeA(3, 1, 2);

    }

    private void addState1Record(int a1, int b1, boolean b2) {
        HollowObjectWriteRecord bRec = new HollowObjectWriteRecord(typeBSchema_state1);

        bRec.setInt("b1", b1);
        bRec.setBoolean("b2", b2);

        int bOrdinal = writeStateEngine.add("TypeB", bRec);

        HollowObjectWriteRecord aRec = new HollowObjectWriteRecord(typeASchema);

        aRec.setInt("a1", a1);
        aRec.setReference("a2", bOrdinal);

        writeStateEngine.add("TypeA", aRec);
    }

    private void addState2Record(int a1, int b1) {
        HollowObjectWriteRecord bRec = new HollowObjectWriteRecord(typeBSchema_state2);

        bRec.setInt("b1", b1);

        int bOrdinal = writeStateEngine.add("TypeB", bRec);

        HollowObjectWriteRecord aRec = new HollowObjectWriteRecord(typeASchema);

        aRec.setInt("a1", a1);
        aRec.setReference("a2", bOrdinal);

        writeStateEngine.add("TypeA", aRec);
    }

    private void assertTypeA(int ordinal, int a1, int bOrdinal) {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState("TypeA");
        GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(typeState), ordinal);

        Assert.assertEquals(a1, obj.getInt("a1"));
        Assert.assertEquals(bOrdinal, obj.getOrdinal("a2"));
    }

    private void assertTypeB(int ordinal, int b1) {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState("TypeB");
        GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(typeState), ordinal);

        Assert.assertEquals(b1, obj.getInt("b1"));
    }

    @Override
    protected void initializeTypeStates() {
        if(!afterRestoreWithNewSchemas) {
            writeStateEngine.addTypeState(new HollowObjectTypeWriteState(typeASchema));
            writeStateEngine.addTypeState(new HollowObjectTypeWriteState(typeBSchema_state1));
        } else {
            writeStateEngine.addTypeState(new HollowObjectTypeWriteState(typeASchema));
            writeStateEngine.addTypeState(new HollowObjectTypeWriteState(typeBSchema_state2));
        }
    }

}
