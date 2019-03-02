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
import com.netflix.hollow.core.type.accessor.LongDataAccessor;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class LongDataAccessorTest extends AbstractPrimitiveTypeDataAccessorTest<Long> {

    @Override
    protected Class<Long> getDataModelTestClass() {
        return Long.class;
    }

    @Override
    protected Long getData(HollowObjectTypeReadState readState, int ordinal) {
        GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(readState), ordinal);
        return obj.getLong("value");
    }

    @Test
    public void test() throws IOException {
        addRecord(new Long(1));
        addRecord(new Long(2));
        addRecord(new Long(3));

        roundTripSnapshot();
        {
            LongDataAccessor dAccessor = new LongDataAccessor(readStateEngine, new PrimitiveTypeTestAPI(readStateEngine));
            Assert.assertEquals(3, dAccessor.getAddedRecords().size());
            assertList(dAccessor.getAddedRecords(), Arrays.<Long>asList(new Long(1), new Long(2), new Long(3)));
            Assert.assertTrue(dAccessor.getRemovedRecords().isEmpty());
            Assert.assertTrue(dAccessor.getUpdatedRecords().isEmpty());
        }

        writeStateEngine.prepareForNextCycle(); /// not necessary to call, but needs to be a no-op.

        addRecord(new Long(1));
        // addRecord(new Long(2)); // removed
        addRecord(new Long(3));
        addRecord(new Long(1000)); // added
        addRecord(new Long(0)); // added

        roundTripDelta();
        {
            LongDataAccessor dAccessor = new LongDataAccessor(readStateEngine, new PrimitiveTypeTestAPI(readStateEngine));
            Assert.assertEquals(2, dAccessor.getAddedRecords().size());
            assertList(dAccessor.getAddedRecords(), Arrays.asList(new Long(1000), new Long(0)));
            Assert.assertEquals(1, dAccessor.getRemovedRecords().size());
            assertList(dAccessor.getRemovedRecords(), Arrays.asList(new Long(2)));
            Assert.assertEquals(0, dAccessor.getUpdatedRecords().size());
        }

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState("Long");
        Assert.assertEquals(4, typeState.maxOrdinal());

        assertObject(typeState, 0, new Long(1));
        assertObject(typeState, 1, new Long(2)); /// this was "removed", but the data hangs around as a "ghost" until the following cycle.
        assertObject(typeState, 2, new Long(3));
        assertObject(typeState, 3, new Long(1000));
        assertObject(typeState, 4, new Long(0));

        roundTripDelta(); // remove everything
        {
            LongDataAccessor dAccessor = new LongDataAccessor(readStateEngine, new PrimitiveTypeTestAPI(readStateEngine));
            Assert.assertEquals(0, dAccessor.getAddedRecords().size());
            Assert.assertEquals(4, dAccessor.getRemovedRecords().size());
            assertList(dAccessor.getRemovedRecords(), Arrays.asList(new Long(1), new Long(3), new Long(1000), new Long(0)));
            Assert.assertEquals(0, dAccessor.getUpdatedRecords().size());
        }

        assertObject(typeState, 0, new Long(1)); /// all records were "removed", but again hang around until the following cycle.
        // assertObject(typeState, 1, new Long(2)); /// this record should now be disappeared.
        assertObject(typeState, 2, new Long(3)); /// "ghost"
        assertObject(typeState, 3, new Long(1000)); /// "ghost"
        assertObject(typeState, 4, new Long(0)); /// "ghost"

        Assert.assertEquals(4, typeState.maxOrdinal());

        addRecord(new Long(634));
        addRecord(new Long(0));

        roundTripDelta();
        {
            LongDataAccessor dAccessor = new LongDataAccessor(readStateEngine, new PrimitiveTypeTestAPI(readStateEngine));
            Assert.assertEquals(2, dAccessor.getAddedRecords().size());
            assertList(dAccessor.getAddedRecords(), Arrays.asList(new Long(634), new Long(0)));
            Assert.assertEquals(0, dAccessor.getRemovedRecords().size());
            Assert.assertEquals(0, dAccessor.getUpdatedRecords().size());
        }

        Assert.assertEquals(1, typeState.maxOrdinal());
        assertObject(typeState, 0, new Long(634)); /// now, since all records were removed, we can recycle the ordinal "0", even though it was a "ghost" in the last cycle.
        assertObject(typeState, 1, new Long(0)); /// even though new Long(0) had an equivalent record in the previous cycle at ordinal "4", it is now assigned to recycled ordinal "1".
    }
}