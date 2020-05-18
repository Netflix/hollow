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
package com.netflix.hollow.core.read.map;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class HollowMapLargeTest extends AbstractStateEngineTest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testSnapshotSmall() throws IOException {
        testSnapshot(1 << 9, 1 << 8, 1 << 22);
    }

    @Ignore
    @Test
    // This test configuration can use up lots of memory
    public void testSnapshotLarge() throws IOException {
        testSnapshot(1 << 9, 1 << 16, 1 << 22);
    }

    void testSnapshot(int nMaps, int maxOrdinal, int initialValue) throws IOException {
        for (int n = 0; n < nMaps; n++) {
            int v = initialValue - n;
            HollowMapWriteRecord rec = new HollowMapWriteRecord();
            for (int i = 0; i < maxOrdinal; i++, v--) {
                rec.addEntry(i, v);
            }
            writeStateEngine.add("TestMap", rec);
        }

        roundTripSnapshot();

        HollowMapTypeReadState typeState = (HollowMapTypeReadState)
                readStateEngine.getTypeState("TestMap");

        for (int n = 0; n < nMaps; n++) {
            int l = typeState.size(n);
            Assert.assertEquals(maxOrdinal, l);

            int v = initialValue - n;
            for (int i = 0; i < maxOrdinal; i++, v--) {
                Assert.assertEquals(n + " " + i, v, typeState.get(n, i));
            }
        }
    }

    @Test
    public void testDeltaSmall() throws IOException {
        testDelta(1 << 9, 1 << 8, 1 << 22);
    }

    @Ignore
    @Test
    // This test configuration can use up lots of memory
    public void testDeltaLarge() throws IOException {
        testDelta(1 << 9, 1 << 16, 1 << 22);
    }

    void testDelta(int nMaps, int maxOrdinal, int initialValue) throws IOException {
        {
            int v = initialValue;
            HollowMapWriteRecord rec = new HollowMapWriteRecord();
            for (int i = 0; i < maxOrdinal; i++, v--) {
                rec.addEntry(i, v);
            }
        }

        roundTripSnapshot();

        for (int n = 0; n < nMaps; n++) {
            int v = initialValue - n;
            HollowMapWriteRecord rec = new HollowMapWriteRecord();
            for (int i = 0; i < maxOrdinal; i++, v--) {
                rec.addEntry(i, v);
            }
            writeStateEngine.add("TestMap", rec);
        }

        roundTripDelta();

        HollowMapTypeReadState typeState = (HollowMapTypeReadState)
                readStateEngine.getTypeState("TestMap");

        for (int n = 0; n < nMaps; n++) {
            int l = typeState.size(n);
            Assert.assertEquals(maxOrdinal, l);

            int v = initialValue - n;
            for (int i = 0; i < maxOrdinal; i++, v--) {
                Assert.assertEquals(n + " " + i, v, typeState.get(n, i));
            }
        }
    }

    @Override
    protected void initializeTypeStates() {
        HollowMapTypeWriteState writeState = new HollowMapTypeWriteState(
                new HollowMapSchema("TestMap", "TestKey", "TestValue"));
        writeStateEngine.addTypeState(writeState);
    }
}
