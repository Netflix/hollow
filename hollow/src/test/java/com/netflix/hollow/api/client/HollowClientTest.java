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
package com.netflix.hollow.api.client;

import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class HollowClientTest {

    private HollowWriteStateEngine writeEngine;
    private HollowObjectTypeWriteState typeWriteState;
    private HollowObjectSchema schema;
    private HollowBlobWriter writer;

    private HollowClient client;

    private final ByteArrayOutputStream snapshot1 = new ByteArrayOutputStream();
    private final ByteArrayOutputStream delta1 = new ByteArrayOutputStream();
    private final ByteArrayOutputStream delta2 = new ByteArrayOutputStream();
    private final ByteArrayOutputStream delta3 = new ByteArrayOutputStream();

    @Before
    public void setUp() throws IOException {
        this.writeEngine = new HollowWriteStateEngine();

        this.schema = new HollowObjectSchema("TestObject", 2);
        schema.addField("f1", FieldType.INT);
        schema.addField("f2", FieldType.STRING);

        this.typeWriteState = new HollowObjectTypeWriteState(schema);
        writeEngine.addTypeState(typeWriteState);

        writer = new HollowBlobWriter(writeEngine);

        snapshot1.reset();
        delta1.reset();
        delta2.reset();
        delta3.reset();

        client = new HollowClient.Builder()
                              .withBlobRetriever(new FakeHollowBlobRetriever())
                              .withMemoryConfig(new HollowClientMemoryConfig.SpecifiedConfig(true, false, 10000L, 10000L))
                              .build();

        createChain();
    }

    @Test
    public void testClientAPIHoldsLongLivedReferences() throws IOException {
        client.triggerRefreshTo(1);

        HollowAPI api = client.getAPI();

        HollowObjectTypeDataAccess dataAccess = (HollowObjectTypeDataAccess) api.getDataAccess().getTypeDataAccess("TestObject");

        client.triggerRefreshTo(2);
        client.triggerRefreshTo(3);
        client.triggerRefreshTo(4);

        Assert.assertEquals(1, dataAccess.readInt(1, 0));
        Assert.assertEquals("one", dataAccess.readString(1, 1));
        Assert.assertEquals(3, dataAccess.readInt(3, 0));
        Assert.assertEquals("three", dataAccess.readString(3, 1));
    }

    private void createChain() throws IOException {
        addRecord(0, "zero");
        addRecord(1, "one");
        addRecord(2, "two");
        addRecord(3, "three");
        addRecord(4, "four");
        addRecord(5, "five");

        writer.writeSnapshot(snapshot1);
        writeEngine.prepareForNextCycle();

        addRecord(0, "zero");
        // addRecord(1, "one");
        addRecord(2, "two");
        addRecord(3, "three");
        // addRecord(4, "four");
        addRecord(5, "five");
        addRecord(6, "six");

        writer.writeDelta(delta1);
        writeEngine.prepareForNextCycle();

        addRecord(0, "zero");
        addRecord(7, "seven"); // addRecord(1, "one");
        // addRecord(2, "two");
        addRecord(3, "three");
        addRecord(8, "eight"); // addRecord(4, "four");
        // addRecord(5, "five");
        // addRecord(6, "six");

        writer.writeDelta(delta2);
        writeEngine.prepareForNextCycle();


        addRecord(0, "zero");
        addRecord(7, "seven"); // addRecord(1, "one");
        addRecord(9, "nine"); // addRecord(2, "two");
        addRecord(3, "three");
        addRecord(8, "eight"); // addRecord(4, "four");
        // addRecord(5, "five");
        // addRecord(6, "six");

        writer.writeDelta(delta3);
        writeEngine.prepareForNextCycle();
    }


    private final void addRecord(int f1, String f2) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        rec.setInt("f1", f1);
        rec.setString("f2", f2);
        writeEngine.add("TestObject", rec);
    }


    private class FakeHollowBlobRetriever implements HollowBlobRetriever {

        @Override
        public HollowBlob retrieveSnapshotBlob(long desiredVersion) {
            return new HollowBlob(1) {
                public InputStream getInputStream() throws IOException {
                    return new ByteArrayInputStream(snapshot1.toByteArray());
                }
            };
        }

        @Override
        public HollowBlob retrieveDeltaBlob(long currentVersion) {
            byte[] data = null;
            if(currentVersion == 1)  data = delta1.toByteArray();
            if(currentVersion == 2)  data = delta2.toByteArray();
            if(currentVersion == 3)  data = delta3.toByteArray();

            final byte b[] = data;

            return new HollowBlob(currentVersion, currentVersion + 1) {
                public InputStream getInputStream() throws IOException {
                    return new ByteArrayInputStream(b);
                }
            };
        }

        @Override
        public HollowBlob retrieveReverseDeltaBlob(long currentVersion) {
            throw new UnsupportedOperationException();
        }

    }

}
