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

import com.netflix.hollow.api.objects.delegate.HollowObjectGenericDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.type.accessor.BooleanDataAccessor;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class BooleanDataAccessorTest extends AbstractPrimitiveTypeDataAccessorTest<Boolean> {

    @Override
    protected Class<Boolean> getDataModelTestClass() {
        return Boolean.class;
    }

    @Override
    protected Boolean getData(HollowObjectTypeReadState readState, int ordinal) {
        GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(readState), ordinal);
        return obj.getBoolean("value");
    }

    @Test
    public void test() throws IOException {
        addRecord(new Boolean(true));
        addRecord(new Boolean(false));

        roundTripSnapshot();
        {
            BooleanDataAccessor dAccessor = new BooleanDataAccessor(readStateEngine, new PrimitiveTypeTestAPI(readStateEngine));
            Assert.assertEquals(2, dAccessor.getAddedRecords().size());
            assertList(dAccessor.getAddedRecords(), Arrays.<Boolean>asList(true, false));
            Assert.assertTrue(dAccessor.getRemovedRecords().isEmpty());
            Assert.assertTrue(dAccessor.getUpdatedRecords().isEmpty());
        }

        writeStateEngine.prepareForNextCycle(); /// not necessary to call, but needs to be a no-op.

        addRecord(new Boolean(true));
        // addRecord(new Boolean(false)); // removed

        roundTripDelta();
        {
            BooleanDataAccessor dAccessor = new BooleanDataAccessor(readStateEngine, new PrimitiveTypeTestAPI(readStateEngine));
            Assert.assertEquals(0, dAccessor.getAddedRecords().size());
            Assert.assertEquals(1, dAccessor.getRemovedRecords().size());
            assertList(dAccessor.getRemovedRecords(), Arrays.asList(false));
            Assert.assertEquals(0, dAccessor.getUpdatedRecords().size());
        }

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState("Boolean");
        Assert.assertEquals(1, typeState.maxOrdinal());

        assertObject(typeState, 0, true);
        assertObject(typeState, 1, false); /// this was "removed", but the data hangs around as a "ghost" until the following cycle.

        roundTripDelta(); // remove everything
        {
            BooleanDataAccessor dAccessor = new BooleanDataAccessor(readStateEngine, new PrimitiveTypeTestAPI(readStateEngine));
            Assert.assertEquals(0, dAccessor.getAddedRecords().size());
            Assert.assertEquals(1, dAccessor.getRemovedRecords().size());
            assertList(dAccessor.getRemovedRecords(), Arrays.asList(true));
            Assert.assertEquals(0, dAccessor.getUpdatedRecords().size());
        }

        assertObject(typeState, 0, true); /// all records were "removed", but again hang around until the following cycle.
        // assertObject(typeState, 1, false); /// this record should now be disappeared.

        Assert.assertEquals(0, typeState.maxOrdinal());

        addRecord(new Boolean(false));
        addRecord(new Boolean(true));

        roundTripDelta();
        {
            BooleanDataAccessor dAccessor = new BooleanDataAccessor(readStateEngine, new PrimitiveTypeTestAPI(readStateEngine));
            Assert.assertEquals(2, dAccessor.getAddedRecords().size());
            assertList(dAccessor.getAddedRecords(), Arrays.asList(false, true));
            Assert.assertEquals(0, dAccessor.getRemovedRecords().size());
            Assert.assertEquals(0, dAccessor.getUpdatedRecords().size());
        }

        Assert.assertEquals(1, typeState.maxOrdinal());
        assertObject(typeState, 0, false); /// now, since all records were removed, we can recycle the ordinal "0", even though it was a "ghost" in the last cycle.
        assertObject(typeState, 1, true); /// even though "zero" had an equivalent record in the previous cycle at ordinal "4", it is now assigned to recycled ordinal "1".
    }
}