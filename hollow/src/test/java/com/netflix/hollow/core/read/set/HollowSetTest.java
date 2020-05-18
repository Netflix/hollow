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
package com.netflix.hollow.core.read.set;

import static com.netflix.hollow.core.read.iterator.HollowOrdinalIterator.NO_MORE_ORDINALS;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HollowSetTest extends AbstractStateEngineTest {

    @Test
    public void testContains() throws IOException {
        addRecord(10, 20, 30);

        roundTripSnapshot();

        HollowSetTypeReadState typeState = (HollowSetTypeReadState) readStateEngine.getTypeState("TestSet");

        HollowOrdinalIterator iter = typeState.potentialMatchOrdinalIterator(0, 20);

        boolean foundValue = false;

        int ordinal = iter.next();
        while(ordinal != NO_MORE_ORDINALS) {
            if(ordinal == 20)
                foundValue = true;
            ordinal = iter.next();
        }

        if(!foundValue)
            Assert.fail("Did not find value in PotentialMatchOrdinalIterator");
    }

    @Test
    public void testSingleEmptySet() throws IOException {
        addRecord();

        roundTripSnapshot();

        HollowSetTypeReadState typeState = (HollowSetTypeReadState) readStateEngine.getTypeState("TestSet");

        Assert.assertEquals(0, typeState.size(0));
        Assert.assertEquals(0, typeState.maxOrdinal());
    }

    @Test
    public void testSingleElementWith0Ordinal() throws IOException {
        addRecord(0);

        roundTripSnapshot();

        HollowSetTypeReadState typeState = (HollowSetTypeReadState) readStateEngine.getTypeState("TestSet");

        Assert.assertEquals(1, typeState.size(0));
        Assert.assertEquals(0, typeState.maxOrdinal());

        HollowOrdinalIterator iter = typeState.ordinalIterator(0);

        Assert.assertEquals(0, iter.next());
        Assert.assertEquals(NO_MORE_ORDINALS, iter.next());
    }

    @Test
    public void testStaleReferenceException() throws IOException {
        roundTripSnapshot();

        readStateEngine.invalidate();

        HollowSetTypeReadState typeState = (HollowSetTypeReadState) readStateEngine.getTypeState("TestSet");

        try {
            Assert.assertEquals(0, typeState.size(100));
            Assert.fail("Should have thrown Exception");
        } catch(NullPointerException expected) { }
    }


    private void addRecord(int... ordinals) {
        HollowSetWriteRecord rec = new HollowSetWriteRecord();

        for(int i=0;i<ordinals.length;i++) {
            rec.addElement(ordinals[i]);
        }

        writeStateEngine.add("TestSet", rec);
    }

    @Override
    protected void initializeTypeStates() {
        HollowSetTypeWriteState writeState = new HollowSetTypeWriteState(new HollowSetSchema("TestSet", "TestObject"));
        writeStateEngine.addTypeState(writeState);
    }

}
