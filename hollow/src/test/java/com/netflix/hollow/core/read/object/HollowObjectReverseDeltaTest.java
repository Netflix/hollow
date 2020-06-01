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
package com.netflix.hollow.core.read.object;

import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowObjectReverseDeltaTest {

    HollowObjectSchema schema;
    HollowWriteStateEngine writeEngine;
    HollowReadStateEngine readEngine;

    @Before
    public void setUp() {
        schema = new HollowObjectSchema("test", 1);
        schema.addField("field", FieldType.INT);

        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema);
        writeEngine = new HollowWriteStateEngine();
        writeEngine.addTypeState(writeState);

        readEngine = new HollowReadStateEngine();
    }

    @Test
    public void test() throws IOException {
        addWriteRecord(100);
        addWriteRecord(101);
        addWriteRecord(102);
        addWriteRecord(103);

        writeEngine.prepareForWrite();
        writeEngine.prepareForNextCycle();

        addWriteRecord(100);
        addWriteRecord(101);
        addWriteRecord(103);

        writeEngine.prepareForWrite();
        byte reverseDelta3[] = createReverseDelta();
        writeEngine.prepareForNextCycle();

        addWriteRecord(101);

        writeEngine.prepareForWrite();
        byte reverseDelta2[] = createReverseDelta();
        writeEngine.prepareForNextCycle();

        addWriteRecord(100);
        addWriteRecord(101);

        writeEngine.prepareForWrite();
        byte reverseDelta1[] = createReverseDelta();
        byte snapshot[] = createSnapshot();

        HollowBlobReader reader = new HollowBlobReader(readEngine);

        reader.readSnapshot(HollowBlobInput.serial(snapshot));
        assertState(100, 101);
        reader.applyDelta(HollowBlobInput.serial(reverseDelta1));
        assertState(-100, 101);
        reader.applyDelta(HollowBlobInput.serial(reverseDelta2));
        assertState(100, 101, -1, 103);
        reader.applyDelta(HollowBlobInput.serial(reverseDelta3));
        assertState(100, 101, 102, 103);
    }

    private void assertState(int... expectedValuesInOrdinalPosition) {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readEngine.getTypeState("test");
        PopulatedOrdinalListener listener = typeState.getListener(PopulatedOrdinalListener.class);
        Assert.assertEquals(expectedValuesInOrdinalPosition.length, listener.getPopulatedOrdinals().length());

        for(int i=0;i<expectedValuesInOrdinalPosition.length;i++) {
            if(expectedValuesInOrdinalPosition[i] != -1) {
                if(expectedValuesInOrdinalPosition[i] < 0)
                    Assert.assertFalse(listener.getPopulatedOrdinals().get(i));
                else
                    Assert.assertTrue(listener.getPopulatedOrdinals().get(i));

                int expectedValue = Math.abs(expectedValuesInOrdinalPosition[i]);

                Assert.assertEquals(expectedValue, typeState.readInt(i, 0));
            }
        }
    }

    private void addWriteRecord(int value) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        rec.setInt("field", value);
        writeEngine.add("test", rec);
    }

    private byte[] createReverseDelta() throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.writeReverseDelta(baos);
        return baos.toByteArray();
    }

    private byte[] createSnapshot() throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.writeSnapshot(baos);
        return baos.toByteArray();
    }



}
