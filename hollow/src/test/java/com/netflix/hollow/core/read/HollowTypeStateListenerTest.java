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
package com.netflix.hollow.core.read;

import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.BitSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowTypeStateListenerTest {

    HollowWriteStateEngine writeStateEngine;
    HollowBlobWriter blobWriter;
    ByteArrayOutputStream baos;
    HollowObjectSchema schema;

    HollowReadStateEngine readStateEngine;
    HollowBlobReader blobReader;

    PopulatedOrdinalListener listener;

    @Before
    public void setUp() {
        listener = new PopulatedOrdinalListener();

        writeStateEngine = new HollowWriteStateEngine();
        blobWriter = new HollowBlobWriter(writeStateEngine);
        baos = new ByteArrayOutputStream();

        schema = new HollowObjectSchema("TestObject", 2);
        schema.addField("f1", FieldType.INT);
        schema.addField("f2", FieldType.STRING);

        readStateEngine = new HollowReadStateEngine();
        readStateEngine.addTypeListener("TestObject", listener);
        blobReader = new HollowBlobReader(readStateEngine);
    }


    @Test
    public void test() throws IOException {
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema);
        writeStateEngine.addTypeState(writeState);


        addRecord(writeState, 1, "one");
        addRecord(writeState, 2, "two");
        addRecord(writeState, 3, "three");

        roundTripSnapshot();

        addRecord(writeState, 1, "one");
        addRecord(writeState, 3, "three");
        addRecord(writeState, 1000, "one thousand");
        addRecord(writeState, 0, "zero");

        roundTripDelta();

        BitSet populatedBitSet = listener.getPopulatedOrdinals();

        Assert.assertEquals(4, populatedBitSet.cardinality());

        Assert.assertTrue(populatedBitSet.get(0));
        Assert.assertTrue(populatedBitSet.get(2));
        Assert.assertTrue(populatedBitSet.get(3));
        Assert.assertTrue(populatedBitSet.get(4));
    }

    private void addRecord(HollowObjectTypeWriteState writeState, int intVal, String strVal) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);

        rec.setInt("f1", intVal);
        rec.setString("f2", strVal);

        writeState.add(rec);
    }

    private void roundTripSnapshot() throws IOException {
        writeStateEngine.prepareForWrite();
        blobWriter.writeSnapshot(baos);
        writeStateEngine.prepareForNextCycle();
        blobReader.readSnapshot(HollowBlobInput.serial(baos.toByteArray()));
        baos.reset();
    }

    private void roundTripDelta() throws IOException {
        writeStateEngine.prepareForWrite();
        blobWriter.writeDelta(baos);
        writeStateEngine.prepareForNextCycle();
        blobReader.applyDelta(HollowBlobInput.serial(baos.toByteArray()));
        baos.reset();
    }

}
