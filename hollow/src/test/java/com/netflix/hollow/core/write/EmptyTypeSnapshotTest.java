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

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import java.io.IOException;
import org.junit.Test;

public class EmptyTypeSnapshotTest extends AbstractStateEngineTest {


    @Test
    public void test() throws IOException {
        roundTripSnapshot();
    }

    @Override
    protected void initializeTypeStates() {
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(new HollowObjectSchema("TestObject", 0)));
        writeStateEngine.addTypeState(new HollowListTypeWriteState(new HollowListSchema("TestList", "TestObject")));
        writeStateEngine.addTypeState(new HollowSetTypeWriteState(new HollowSetSchema("TestSet", "TestObject")));
        writeStateEngine.addTypeState(new HollowMapTypeWriteState(new HollowMapSchema("TestMap", "TestObject", "TestObject")));
    }

}
