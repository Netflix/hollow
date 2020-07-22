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
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowBlobHeaderTest {
    private static final String VERSION_NAME = "version";
    private static final String VERSION_VALUE = "1234";
    private static final String JAR_VERSION_NAME = "jarversion";
    private static final String JAR_VERSION_VALUE = "12.34";
    private static final String HEADER_VAL2 = "val2";
    private static final String HEADER_VAL1 = "val1";
    private static final String HEADER_NAME1 = "name1";
    private static final String HEADER_NAME2 = "name2";
    private HollowWriteStateEngine writeStateEngine;
    private HollowBlobWriter blobWriter;
    private ByteArrayOutputStream baos;
    private HollowReadStateEngine readStateEngine;
    private HollowBlobReader blobReader;

    @Before
    public void setUp() {
        writeStateEngine = new HollowWriteStateEngine();
        writeStateEngine.addHeaderTag(VERSION_NAME, VERSION_VALUE);
        writeStateEngine.addHeaderTag(JAR_VERSION_NAME, JAR_VERSION_VALUE);
        blobWriter = new HollowBlobWriter(writeStateEngine);
        baos = new ByteArrayOutputStream();
        readStateEngine = new HollowReadStateEngine();
        blobReader = new HollowBlobReader(readStateEngine);
    }

    @Test
    public void writeAndReadHeadersForSnapshot() throws IOException {
        roundTripSnapshot();
        Assert.assertEquals(VERSION_VALUE, readStateEngine.getHeaderTag(VERSION_NAME));
        Assert.assertEquals(JAR_VERSION_VALUE, readStateEngine.getHeaderTag(JAR_VERSION_NAME));
    }

    @Test
    public void writeAndReadHeadersForDelta() throws IOException {
        roundTripDelta();
        Assert.assertEquals(VERSION_VALUE, readStateEngine.getHeaderTag(VERSION_NAME));
        Assert.assertEquals(JAR_VERSION_VALUE, readStateEngine.getHeaderTag(JAR_VERSION_NAME));
    }

    @Test
    public void writeAndReadHeaderMapForSnapshot() throws IOException {
        Map<String, String> headerTags = new HashMap<String, String>();
        headerTags.put(HEADER_NAME1, HEADER_VAL1);
        headerTags.put(HEADER_NAME2, HEADER_VAL2);
        writeStateEngine.addHeaderTags(headerTags);
        roundTripSnapshot();
        Assert.assertEquals(VERSION_VALUE, readStateEngine.getHeaderTag(VERSION_NAME));
        Assert.assertEquals(JAR_VERSION_VALUE, readStateEngine.getHeaderTag(JAR_VERSION_NAME));
        Assert.assertEquals(HEADER_VAL1, readStateEngine.getHeaderTag(HEADER_NAME1));
        Assert.assertEquals(HEADER_VAL2, readStateEngine.getHeaderTag(HEADER_NAME2));
    }

    private void roundTripSnapshot() throws IOException {
        blobWriter.writeSnapshot(baos);
        writeStateEngine.prepareForNextCycle();
        blobReader.readSnapshot(HollowBlobInput.serial(baos.toByteArray()));
        baos.reset();
    }

    private void roundTripDelta() throws IOException {
        blobWriter.writeSnapshot(baos);
        writeStateEngine.prepareForNextCycle();
        blobReader.readSnapshot(HollowBlobInput.serial(baos.toByteArray()));
        baos.reset();
        blobWriter.writeDelta(baos);
        writeStateEngine.prepareForNextCycle();
        blobReader.applyDelta(HollowBlobInput.serial(baos.toByteArray()));
        baos.reset();
    }
}
