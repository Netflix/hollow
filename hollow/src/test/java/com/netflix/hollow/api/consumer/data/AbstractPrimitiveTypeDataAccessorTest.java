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
 */
package com.netflix.hollow.api.consumer.data;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;

public abstract class AbstractPrimitiveTypeDataAccessorTest<T> extends AbstractStateEngineTest {
    HollowObjectMapper objectMapper;

    protected abstract Class<T> getDataModelTestClass();

    protected abstract T getData(HollowObjectTypeReadState readState, int ordinal);

    @Override
    protected void initializeTypeStates() {
        objectMapper = new HollowObjectMapper(writeStateEngine);
        objectMapper.initializeTypeState(getDataModelTestClass());
    }

    protected void addRecord(Object obj) {
        objectMapper.add(obj);
    }

    protected void assertObject(HollowObjectTypeReadState readState, int ordinal, T expectedValue) {
        Object obj = getData(readState, ordinal);

        Assert.assertEquals(expectedValue, obj);
    }

    protected void assertList(Collection<T> listOfObj, List<T> expectedObjs) {
        int i = 0;
        for(T obj : listOfObj) {
            Object expectedObj = expectedObjs.get(i++);
            Assert.assertEquals(expectedObj, obj);
        }
    }

//    protected void assertUpdatedList(Collection<UpdatedRecord<T>> listOfObj, List<T> beforeValues, List<T> afterValues) {
//        int i = 0;
//        for (UpdatedRecord<T> obj : listOfObj) {
//            T before = obj.getBefore();
//            T after = obj.getAfter();
//            Assert.assertNotEquals(before, after);
//
//            T expBefore= beforeValues.get(i);
//            T expAfter = afterValues.get(i++);
//            Assert.assertNotEquals(expBefore, expAfter);
//            Assert.assertEquals(expBefore, before);
//            Assert.assertEquals(expAfter, after);
//        }
//    }
}
