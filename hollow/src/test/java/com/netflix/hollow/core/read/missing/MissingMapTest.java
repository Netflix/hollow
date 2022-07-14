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
package com.netflix.hollow.core.read.missing;

import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.generic.GenericHollowMap;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;
import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class MissingMapTest extends AbstractStateEngineTest {
    @Test
    public void testCompletelyMissingMap() throws IOException {
        roundTripSnapshot();
        readStateEngine.setMissingDataHandler(new FakeMissingDataHandler());


        GenericHollowMap map = (GenericHollowMap) GenericHollowRecordHelper.instantiate(readStateEngine, "MissingMap", 0);

        Assert.assertEquals(2, map.size());
        Assert.assertTrue(map.containsKey(new FakeMissingHollowRecord(new HollowObjectMissingDataAccess(readStateEngine, "MissingObject"), 2)));
        Assert.assertEquals(300, map.get(new FakeMissingHollowRecord(new HollowObjectMissingDataAccess(readStateEngine, "MissingObject"), 3)).getOrdinal());
        Assert.assertFalse(map.containsKey(new FakeMissingHollowRecord(new HollowObjectMissingDataAccess(readStateEngine, "MissingObject"), 0)));
        Assert.assertNull(map.get(new FakeMissingHollowRecord(new HollowObjectMissingDataAccess(readStateEngine, "MissingObject"), 4)));

        Iterator<Map.Entry<HollowRecord, HollowRecord>> rec = map.entrySet().iterator();

        Assert.assertTrue(rec.hasNext());
        Map.Entry<HollowRecord, HollowRecord> next = rec.next();
        Assert.assertEquals(2, next.getKey().getOrdinal());
        Assert.assertEquals("MissingObject", next.getKey().getSchema().getName());
        Assert.assertEquals(200, next.getValue().getOrdinal());
        Assert.assertEquals("MissingObject", next.getValue().getSchema().getName());
        Assert.assertTrue(rec.hasNext());
        next = rec.next();
        Assert.assertEquals(3, next.getKey().getOrdinal());
        Assert.assertEquals("MissingObject", next.getKey().getSchema().getName());
        Assert.assertEquals(300, next.getValue().getOrdinal());
        Assert.assertEquals("MissingObject", next.getValue().getSchema().getName());
        Assert.assertEquals(300, map.get(next.getKey()).getOrdinal());
        Assert.assertEquals("MissingObject", map.get(next.getKey()).getSchema().getName());
        Assert.assertFalse(rec.hasNext());
    }

    private class FakeMissingDataHandler extends DefaultMissingDataHandler {
        @Override
        public HollowSchema handleSchema(String type) {
            if("MissingMap".equals(type))
                return new HollowMapSchema("MissingMap", "MissingObject", "MissingObject");
            if("MissingObject".equals(type))
                return new HollowObjectSchema("MissingObject", 0);
            return null;
        }

        @Override
        public int handleMapSize(String type, int ordinal) {
            return 2;
        }

        @Override
        public HollowMapEntryOrdinalIterator handleMapOrdinalIterator(String type, int ordinal) {
            return new HollowMapEntryOrdinalIterator() {
                private final int keys[] = {2, 3};
                private final int values[] = {200, 300};
                private int counter = -1;

                @Override
                public boolean next() {
                    return ++counter < keys.length;
                }

                @Override
                public int getValue() {
                    return values[counter];
                }

                @Override
                public int getKey() {
                    return keys[counter];
                }
            };
        }

        @Override
        public HollowMapEntryOrdinalIterator handleMapPotentialMatchOrdinalIterator(String type, int ordinal, int keyHashCode) {
            return handleMapOrdinalIterator(type, ordinal);
        }

        @Override
        public int handleMapGet(String type, int ordinal, int keyOrdinal, int keyOrdinalHashCode) {
            if(ordinal == 2)
                return 200;
            if(ordinal == 3)
                return 300;
            return -1;
        }

    }


    @Override
    protected void initializeTypeStates() {
    }

}
