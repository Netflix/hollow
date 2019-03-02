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
import com.netflix.hollow.core.type.accessor.StringDataAccessor;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class StringDataAccessorTest extends AbstractPrimitiveTypeDataAccessorTest<String> {

    @Override
    protected Class<String> getDataModelTestClass() {
        return String.class;
    }

    @Override
    protected String getData(HollowObjectTypeReadState readState, int ordinal) {
        GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(readState), ordinal);
        return obj.getString("value");
    }

    @Test
    public void test() throws IOException {
        addRecord("one");
        addRecord("two");
        addRecord("three");

        roundTripSnapshot();
        {
            StringDataAccessor dAccessor = new StringDataAccessor(readStateEngine, new PrimitiveTypeTestAPI(readStateEngine));
            Assert.assertEquals(3, dAccessor.getAddedRecords().size());
            assertList(dAccessor.getAddedRecords(), Arrays.<String>asList("one", "two", "three"));
            Assert.assertTrue(dAccessor.getRemovedRecords().isEmpty());
            Assert.assertTrue(dAccessor.getUpdatedRecords().isEmpty());
        }

        writeStateEngine.prepareForNextCycle(); /// not necessary to call, but needs to be a no-op.

        addRecord("one");
        // addRecord("two"); // removed
        addRecord("three");
        addRecord("one thousand"); // added
        addRecord("zero"); // added

        roundTripDelta();
        {
            StringDataAccessor dAccessor = new StringDataAccessor(readStateEngine, new PrimitiveTypeTestAPI(readStateEngine));
            Assert.assertEquals(2, dAccessor.getAddedRecords().size());
            assertList(dAccessor.getAddedRecords(), Arrays.asList("one thousand", "zero"));
            Assert.assertEquals(1, dAccessor.getRemovedRecords().size());
            assertList(dAccessor.getRemovedRecords(), Arrays.asList("two"));
            Assert.assertEquals(0, dAccessor.getUpdatedRecords().size());
        }

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState("String");
        Assert.assertEquals(4, typeState.maxOrdinal());

        assertObject(typeState, 0, "one");
        assertObject(typeState, 1, "two"); /// this was "removed", but the data hangs around as a "ghost" until the following cycle.
        assertObject(typeState, 2, "three");
        assertObject(typeState, 3, "one thousand");
        assertObject(typeState, 4, "zero");

        roundTripDelta(); // remove everything
        {
            StringDataAccessor dAccessor = new StringDataAccessor(readStateEngine, new PrimitiveTypeTestAPI(readStateEngine));
            Assert.assertEquals(0, dAccessor.getAddedRecords().size());
            Assert.assertEquals(4, dAccessor.getRemovedRecords().size());
            assertList(dAccessor.getRemovedRecords(), Arrays.asList("one",  "three", "one thousand", "zero"));
            Assert.assertEquals(0, dAccessor.getUpdatedRecords().size());
        }

        assertObject(typeState, 0, "one"); /// all records were "removed", but again hang around until the following cycle.
        // assertObject(typeState, 1, "two"); /// this record should now be disappeared.
        assertObject(typeState, 2, "three"); /// "ghost"
        assertObject(typeState, 3, "one thousand"); /// "ghost"
        assertObject(typeState, 4, "zero"); /// "ghost"

        Assert.assertEquals(4, typeState.maxOrdinal());

        addRecord("six hundred thirty four");
        addRecord("zero");

        roundTripDelta();
        {
            StringDataAccessor dAccessor = new StringDataAccessor(readStateEngine, new PrimitiveTypeTestAPI(readStateEngine));
            Assert.assertEquals(2, dAccessor.getAddedRecords().size());
            assertList(dAccessor.getAddedRecords(), Arrays.asList("six hundred thirty four", "zero"));
            Assert.assertEquals(0, dAccessor.getRemovedRecords().size());
            Assert.assertEquals(0, dAccessor.getUpdatedRecords().size());
        }

        Assert.assertEquals(1, typeState.maxOrdinal());
        assertObject(typeState, 0, "six hundred thirty four"); /// now, since all records were removed, we can recycle the ordinal "0", even though it was a "ghost" in the last cycle.
        assertObject(typeState, 1, "zero"); /// even though "zero" had an equivalent record in the previous cycle at ordinal "4", it is now assigned to recycled ordinal "1".
    }
}