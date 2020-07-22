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

import com.netflix.hollow.core.memory.ThreadSafeBitSet;
import com.netflix.hollow.core.read.engine.HollowTypeStateListener;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.SnapshotPopulatedOrdinalsReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;
import org.junit.Assert;
import org.junit.Test;

public class SnapshotPopulatedOrdinalsReaderTest {

    @Test
    public void test() throws IOException {
        PopulatedOrdinalListener listener = new PopulatedOrdinalListener();
        ThreadSafeBitSet bitSet = new ThreadSafeBitSet();
        for(int i=0;i<10000;i+=10)
            bitSet.set(i);

        DataInputStream dis = serializeToStream(bitSet);
        SnapshotPopulatedOrdinalsReader.readOrdinals(HollowBlobInput.serial(dis), new HollowTypeStateListener[] { listener });

        BitSet populatedOrdinals = listener.getPopulatedOrdinals();
        Assert.assertEquals(1000, populatedOrdinals.cardinality());
        for(int i=0;i<10000;i+=10)
            Assert.assertTrue(populatedOrdinals.get(i));
    }

    private DataInputStream serializeToStream(ThreadSafeBitSet bitSet) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        bitSet.serializeBitsTo(dos);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInputStream dis = new DataInputStream(bais);
        return dis;
    }

}
