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
package com.netflix.hollow.core;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.IOException;
import org.junit.Before;

public abstract class AbstractStateEngineTest {

    protected HollowWriteStateEngine writeStateEngine;

    protected HollowReadStateEngine readStateEngine;

    protected HollowFilterConfig readFilter;


    @Before
    public void setUp() {
        initWriteStateEngine();
    }

    protected void initWriteStateEngine() {
        writeStateEngine = new HollowWriteStateEngine();

        initializeTypeStates();
    }

    protected void roundTripSnapshot() throws IOException {
        readStateEngine = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine, readFilter);
    }

    protected void roundTripDelta() throws IOException {
        StateEngineRoundTripper.roundTripDelta(writeStateEngine, readStateEngine);
    }

    protected void restoreWriteStateEngineFromReadStateEngine() {
        writeStateEngine = new HollowWriteStateEngine();
        initializeTypeStates();
        writeStateEngine.restoreFrom(readStateEngine);
        writeStateEngine.prepareForNextCycle();
    }

    protected abstract void initializeTypeStates();

}
