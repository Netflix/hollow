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
package com.netflix.hollow.tools.patch.delta;

import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HollowStateDeltaPatcherTest {

    @Test
    public void test() throws IOException {
        HollowReadStateEngine state1 = constructState1();
        HollowReadStateEngine state2 = constructState2();
        
        HollowStateDeltaPatcher patcher = new HollowStateDeltaPatcher(state1, state2);
        
        patcher.prepareInitialTransition();
        
        ByteArrayOutputStream delta1 = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(patcher.getStateEngine());
        writer.writeDelta(delta1);
        
        patcher.prepareFinalTransition();

        ByteArrayOutputStream delta2 = new ByteArrayOutputStream();
        writer = new HollowBlobWriter(patcher.getStateEngine());
        writer.writeDelta(delta2);
        
        patcher.getStateEngine().prepareForNextCycle();
        
        HollowBlobReader reader = new HollowBlobReader(state1);
        reader.applyDelta(HollowBlobInput.serial(delta1.toByteArray()));
        reader.applyDelta(HollowBlobInput.serial(delta2.toByteArray()));

        HollowChecksum checksum1 = HollowChecksum.forStateEngineWithCommonSchemas(state1, state2);
        HollowChecksum checksum2 = HollowChecksum.forStateEngineWithCommonSchemas(state2, state1);
        
        Assert.assertEquals(checksum1, checksum2);
    }
    
    private HollowReadStateEngine constructState1() throws IOException {
        HollowWriteStateEngine stateEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(stateEngine);
        
        mapper.add(new TypeB1(1, 0));
        mapper.add(new TypeA1(1, new TypeB1(2, 1)));
        mapper.add(new TypeA1(2, new TypeB1(3, 2)));
        mapper.add(new TypeA1(999, new TypeB1(999, 3)));
        
        HollowReadStateEngine state1 = StateEngineRoundTripper.roundTripSnapshot(stateEngine);
        
        stateEngine.prepareForNextCycle();
        
        mapper.add(new TypeB1(1, 0));
        mapper.add(new TypeA1(1, new TypeB1(2, 1)));
        mapper.add(new TypeA1(2, new TypeB1(3, 2)));
        mapper.add(new TypeA1(4, new TypeB1(5, 4)));
        mapper.add(new TypeB1(6, 7));

        StateEngineRoundTripper.roundTripDelta(stateEngine, state1);
        
        return state1;
    }
    
    private HollowReadStateEngine constructState2() throws IOException {
        HollowWriteStateEngine stateEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(stateEngine);

        mapper.add(new TypeB2(1, 100));
        mapper.add(new TypeA2(1, new TypeB2(2, 101)));
        mapper.add(new TypeA2(2, new TypeB2(7, 102)));
        mapper.add(new TypeA2(5, new TypeB2(8, 103)));
        mapper.add(new TypeA2(4, new TypeB2(5, 104)));
        mapper.add(new TypeA2(999, new TypeB2(999, 105)));
        mapper.add(new TypeA2(6, new TypeB2(9, 106)));
        
        HollowReadStateEngine state2 = StateEngineRoundTripper.roundTripSnapshot(stateEngine);
        
        stateEngine.prepareForNextCycle();
        
        mapper.add(new TypeB2(1, 100));
        mapper.add(new TypeA2(1, new TypeB2(2, 101)));
        mapper.add(new TypeA2(2, new TypeB2(7, 102)));
        mapper.add(new TypeA2(5, new TypeB2(8, 103)));
        mapper.add(new TypeA2(4, new TypeB2(5, 104)));
        mapper.add(new TypeA2(6, new TypeB2(9, 106)));

        StateEngineRoundTripper.roundTripDelta(stateEngine, state2);
        
        return state2;
    }
    
    
    @SuppressWarnings("unused")
    @HollowTypeName(name="TypeA")
    private static class TypeA1 {
        int a1;
        TypeB1 b;
        
        public TypeA1(int a1, TypeB1 b) {
            this.a1 = a1;
            this.b = b;
        }
    }
    
    @SuppressWarnings("unused")
    @HollowTypeName(name="TypeB")
    private static class TypeB1 {
        int b1;
        int b2;
        
        public TypeB1(int b1, int b2) {
            this.b1 = b1;
            this.b2 = b2;
        }
    }

    @SuppressWarnings("unused")
    @HollowTypeName(name="TypeA")
    private static class TypeA2 {
        int a1;
        TypeB2 b;
        
        public TypeA2(int a1, TypeB2 b) {
            this.a1 = a1;
            this.b = b;
        }
    }
    
    @SuppressWarnings("unused")
    @HollowTypeName(name="TypeB")
    private static class TypeB2 {
        int b1;
        float b3;
        
        public TypeB2(int b1, float b3) {
            this.b1 = b1;
            this.b3 = b3;
        }
    }
}
