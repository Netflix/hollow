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
package com.netflix.hollow.core.write;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class NegativeFloatTest {

    @Test
    public void test() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        for(int i = 0; i < 10; i++) {
            mapper.add(new TypeWithFloat(-200f, i));
        }

        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);

        for(int i = 0; i < 10; i++) {
            GenericHollowObject obj = new GenericHollowObject(readEngine, "TypeWithFloat", i);
            Assert.assertEquals(i, obj.getInt("i"));
        }
    }


    @SuppressWarnings("unused")
    private static class TypeWithFloat {
        float f;
        int i;

        public TypeWithFloat(float f, int i) {
            this.f = f;
            this.i = i;
        }
    }
}
