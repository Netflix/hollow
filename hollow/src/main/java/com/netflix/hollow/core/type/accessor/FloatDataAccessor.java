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
package com.netflix.hollow.core.type.accessor;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumerAPI;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.type.HFloat;

public class FloatDataAccessor extends AbstractHollowDataAccessor<Float> {

    public static final String TYPE = "Float";
    private HollowConsumerAPI.FloatRetriever api;

    public FloatDataAccessor(HollowConsumer consumer) {
        this(consumer.getStateEngine(), (HollowConsumerAPI.FloatRetriever) consumer.getAPI());
    }

    public FloatDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.FloatRetriever api) {
        super(rStateEngine, TYPE, "value");
        this.api = api;
    }

    @Override
    public Float getRecord(int ordinal) {
        HFloat val = api.getHFloat(ordinal);
        return val == null ? null : val.getValueBoxed();
    }
}
