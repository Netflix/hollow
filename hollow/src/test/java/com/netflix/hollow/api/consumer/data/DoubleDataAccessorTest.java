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
import com.netflix.hollow.core.type.accessor.DoubleDataAccessor;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class DoubleDataAccessorTest extends AbstractPrimitiveTypeDataAccessorTest<Double> {

    @Override
    protected Class<Double> getDataModelTestClass() {
        return Double.class;
    }

    @Override
    protected Double getData(HollowObjectTypeReadState readState, int ordinal) {
        GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(readState), ordinal);
        return obj.getDouble("value");
    }

    @Test
    public void test() throws IOException {
        addRecord(new Double(1));
        addRecord(new Double(2));
        addRecord(new Double(3));

        roundTripSnapshot();
        {
            DoubleDataAccessor dAccessor = new DoubleDataAccessor(readStateEngine, new PrimitiveTypeTestAPI(readStateEngine));
            Assert.assertEquals(3, dAccessor.getAddedRecords().size());
            assertList(dAccessor.getAddedRecords(), Arrays.<Double>asList(new Double(1), new Double(2), new Double(3)));
            Assert.assertTrue(dAccessor.getRemovedRecords().isEmpty());
            Assert.assertTrue(dAccessor.getUpdatedRecords().isEmpty());
        }

        writeStateEngine.prepareForNextCycle(); /// not necessary to call, but needs to be a no-op.

        addRecord(new Double(1));
        // addRecord(new Double(2)); // removed
        addRecord(new Double(3));
        addRecord(new Double(1000)); // added
        addRecord(new Double(0)); // added

        roundTripDelta();
        {
            DoubleDataAccessor dAccessor = new DoubleDataAccessor(readStateEngine, new PrimitiveTypeTestAPI(readStateEngine));
            Assert.assertEquals(2, dAccessor.getAddedRecords().size());
            assertList(dAccessor.getAddedRecords(), Arrays.asList(new Double(1000), new Double(0)));
            Assert.assertEquals(1, dAccessor.getRemovedRecords().size());
            assertList(dAccessor.getRemovedRecords(), Arrays.asList(new Double(2)));
            Assert.assertEquals(0, dAccessor.getUpdatedRecords().size());
        }

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState("Double");
        Assert.assertEquals(4, typeState.maxOrdinal());

        assertObject(typeState, 0, new Double(1));
        assertObject(typeState, 1, new Double(2)); /// this was "removed", but the data hangs around as a "ghost" until the following cycle.
        assertObject(typeState, 2, new Double(3));
        assertObject(typeState, 3, new Double(1000));
        assertObject(typeState, 4, new Double(0));

        roundTripDelta(); // remove everything
        {
            DoubleDataAccessor dAccessor = new DoubleDataAccessor(readStateEngine, new PrimitiveTypeTestAPI(readStateEngine));
            Assert.assertEquals(0, dAccessor.getAddedRecords().size());
            Assert.assertEquals(4, dAccessor.getRemovedRecords().size());
            assertList(dAccessor.getRemovedRecords(), Arrays.asList(new Double(1), new Double(3), new Double(1000), new Double(0)));
            Assert.assertEquals(0, dAccessor.getUpdatedRecords().size());
        }

        assertObject(typeState, 0, new Double(1)); /// all records were "removed", but again hang around until the following cycle.
        // assertObject(typeState, 1, new Double(2)); /// this record should now be disappeared.
        assertObject(typeState, 2, new Double(3)); /// "ghost"
        assertObject(typeState, 3, new Double(1000)); /// "ghost"
        assertObject(typeState, 4, new Double(0)); /// "ghost"

        Assert.assertEquals(4, typeState.maxOrdinal());

        addRecord(new Double(634));
        addRecord(new Double(0));

        roundTripDelta();
        {
            DoubleDataAccessor dAccessor = new DoubleDataAccessor(readStateEngine, new PrimitiveTypeTestAPI(readStateEngine));
            Assert.assertEquals(2, dAccessor.getAddedRecords().size());
            assertList(dAccessor.getAddedRecords(), Arrays.asList(new Double(634), new Double(0)));
            Assert.assertEquals(0, dAccessor.getRemovedRecords().size());
            Assert.assertEquals(0, dAccessor.getUpdatedRecords().size());
        }

        Assert.assertEquals(1, typeState.maxOrdinal());
        assertObject(typeState, 0, new Double(634)); /// now, since all records were removed, we can recycle the ordinal "0", even though it was a "ghost" in the last cycle.
        assertObject(typeState, 1, new Double(0)); /// even though new Double(0) had an equivalent record in the previous cycle at ordinal "4", it is now assigned to recycled ordinal "1".
    }
}