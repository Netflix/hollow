/*
 *  Copyright 2020 Netflix, Inc.
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
package com.netflix.hollow.core.read.radial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.tools.checksum.HollowChecksum;

public class RadialDeltaTest {
    
    @Test
    public void testRadialDelta() throws IOException {
        
        byte[] snapshot;
        
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        
        mapper.add(new UnchangedType(100));
        mapper.add(new UnchangedType(200));
        mapper.add(new UnchangedType(300));
        mapper.add(new TestType(1, "one"));
        mapper.add(new TestType(2, "two"));
        mapper.add(new TestType(3, "three"));
        mapper.add(new TestType(4, "four"));
        mapper.add(new TestType(5, "five"));
        
        writeEngine.prepareForWrite();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new HollowBlobWriter(writeEngine).writeSnapshot(baos);
        snapshot = baos.toByteArray();
        
        writeEngine.markHubState();
        
        writeEngine.prepareForNextCycle();
        
        mapper.add(new UnchangedType(100));
        mapper.add(new UnchangedType(200));
        mapper.add(new UnchangedType(300));
        mapper.add(new TestType(1, "one"));
        mapper.add(new TestType(2, "two!"));
        mapper.add(new TestType(3, "three"));
        mapper.add(new TestType(5, "five"));
        mapper.add(new TestType(6, "six"));
        
        writeEngine.prepareForWrite();
        baos = new ByteArrayOutputStream();
        new HollowBlobWriter(writeEngine).writeDelta(baos);
        byte[] delta = baos.toByteArray();
        baos = new ByteArrayOutputStream();
        new HollowBlobWriter(writeEngine).writeRadialDelta(baos);
        byte[] radialDelta = baos.toByteArray();
        
        writeEngine.prepareForNextCycle();
        
        mapper.add(new UnchangedType(100));
        mapper.add(new UnchangedType(200));
        mapper.add(new UnchangedType(300));
        mapper.add(new TestType(1, "one"));
        mapper.add(new TestType(2, "two!!"));
        mapper.add(new TestType(3, "three"));
        mapper.add(new TestType(6, "six"));
        mapper.add(new TestType(7, "seven"));
        
        writeEngine.prepareForWrite();
        baos = new ByteArrayOutputStream();
        new HollowBlobWriter(writeEngine).writeDelta(baos);
        byte[] delta2 = baos.toByteArray();
        baos = new ByteArrayOutputStream();
        new HollowBlobWriter(writeEngine).writeRadialDelta(baos);
        byte[] radialDelta2 = baos.toByteArray();
        
        
        HollowReadStateEngine deltaEngine = new HollowReadStateEngine();
        HollowReadStateEngine radialEngine = new HollowReadStateEngine();
        
        new HollowBlobReader(deltaEngine).readSnapshot(new ByteArrayInputStream(snapshot)); 
        new HollowBlobReader(radialEngine).readSnapshot(new ByteArrayInputStream(snapshot));
        
        new HollowBlobReader(deltaEngine).applyDelta(new ByteArrayInputStream(delta)); 
        new HollowBlobReader(deltaEngine).applyDelta(new ByteArrayInputStream(delta2)); 
        
        new HollowBlobReader(radialEngine).applyRadialDelta(new ByteArrayInputStream(radialDelta2));

        Assert.assertEquals(HollowChecksum.forStateEngine(deltaEngine), HollowChecksum.forStateEngine(radialEngine));
    }
    
    @HollowPrimaryKey(fields="id")
    private static class TestType {
        
        private final int id;
        private final String value;
        
        
        public TestType(int id, String value) {
            this.id = id;
            this.value = value;
        }
    }
    
    private static class UnchangedType {
        
        private final int id;
        
        public UnchangedType(int id) {
            this.id = id;
        }
        
    }

}
