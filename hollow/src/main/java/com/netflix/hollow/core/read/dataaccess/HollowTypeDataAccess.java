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
package com.netflix.hollow.core.read.dataaccess;

import com.netflix.hollow.api.sampling.HollowSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.schema.HollowSchema;

/**
 * A handle for all of the records of a specific type in a Hollow dataset.  The most common type of {@link HollowTypeDataAccess}
 * is a {@link HollowTypeReadState}.
 */
public interface HollowTypeDataAccess {

    /**
     * @return The {@link HollowDataAccess} for the dataset this type belongs to.
     */
    HollowDataAccess getDataAccess();

    /**
     * @return the {@link HollowSchema} for this type.
     */
    HollowSchema getSchema();

    void setSamplingDirector(HollowSamplingDirector director);

    void setFieldSpecificSamplingDirector(HollowFilterConfig fieldSpec, HollowSamplingDirector director);

    void ignoreUpdateThreadForSampling(Thread t);

    HollowSampler getSampler();

    /**
     * Optional operation
     * @return the read state
     */
    HollowTypeReadState getTypeState();

}
