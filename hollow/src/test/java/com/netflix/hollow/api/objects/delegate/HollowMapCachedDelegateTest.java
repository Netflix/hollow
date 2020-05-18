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
package com.netflix.hollow.api.objects.delegate;

import com.netflix.hollow.api.objects.HollowMap;
import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import org.junit.Assert;
import org.junit.Test;

public class HollowMapCachedDelegateTest extends AbstractStateEngineTest {

    @Test
    public void testGetOnEmptyMap() throws Exception {
        addRecord();
        addRecord(10, 20);

        roundTripSnapshot();

        HollowMapCachedDelegate<Integer, Integer> delegate = new HollowMapCachedDelegate<Integer, Integer>((HollowMapTypeReadState)readStateEngine.getTypeState("TestMap"), 0);
        HollowMap<Integer, Integer> map = new HollowMap<Integer, Integer>(delegate, 0) {
            public Integer instantiateKey(int keyOrdinal) {
                return keyOrdinal;
            }
            public Integer instantiateValue(int valueOrdinal) {
                return valueOrdinal;
            }
            public boolean equalsKey(int keyOrdinal, Object testObject) {
                return keyOrdinal == (Integer)testObject;
            }
            public boolean equalsValue(int valueOrdinal, Object testObject) {
                return valueOrdinal == (Integer)testObject;
            }

        };

        Assert.assertNull(delegate.get(map, 0, 10));
    }


    private void addRecord(int... ordinals) {
        HollowMapWriteRecord rec = new HollowMapWriteRecord();

        for(int i=0;i<ordinals.length;i+=2) {
            rec.addEntry(ordinals[i], ordinals[i+1]);
        }

        writeStateEngine.add("TestMap", rec);
    }

    @Override
    protected void initializeTypeStates() {
        HollowMapTypeWriteState writeState = new HollowMapTypeWriteState(new HollowMapSchema("TestMap", "TestKey", "TestValue"));
        writeStateEngine.addTypeState(writeState);
    }

}
