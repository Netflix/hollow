/*
 *
 *  Copyright 2017 Netflix, Inc.
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
package com.netflix.hollow.core.write.objectmapper;


import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class ReuseObjectsAcrossCyclesSafetyTest {

    @Test
    public void reuseObjectsAcrossCycles() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        TypeA a1 = new TypeA(1);
        TypeA a2 = new TypeA(2);

        Assert.assertEquals(0, mapper.add(a1));
        Assert.assertEquals(1, mapper.add(a2));
        Assert.assertEquals(0, mapper.add(a1));

        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);
        writeEngine.prepareForNextCycle();

        Assert.assertEquals(0, mapper.add(a1));
        Assert.assertEquals(2, mapper.add(new TypeA(3)));
        Assert.assertEquals(1, mapper.add(a2));

        StateEngineRoundTripper.roundTripDelta(writeEngine, readEngine);

        Assert.assertEquals(3, readEngine.getTypeState("TypeA").getPopulatedOrdinals().cardinality());
    }

    @SuppressWarnings("unused")
    private static class TypeA {
        int value;

        public TypeA(int value) {
            this.value = value;
        }

        private final long __assigned_ordinal = -1;
    }

}
