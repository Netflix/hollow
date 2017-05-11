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
package com.netflix.hollow.api.consumer;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

/**
 * Alpha API subject to change.
 */
@Deprecated
class ReadStateImpl implements HollowConsumer.ReadState {
    private final long version;
    private final HollowReadStateEngine stateEngine;



    // TODO: timt: should be package protected
    public ReadStateImpl(long version, HollowReadStateEngine stateEngine) {
        this.version = version;
        this.stateEngine = stateEngine;
    }


    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public HollowReadStateEngine getStateEngine() {
        return stateEngine;
    }
}
