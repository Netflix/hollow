/*
 *
 *  Copyright 2016 Netflix, Inc.
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

import com.netflix.hollow.core.schema.HollowSchema;

import com.netflix.hollow.api.sampling.HollowSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;

/**
 * A handle for all of the records of a specific type in a Hollow dataset.  The most common type of {@link HollowTypeDataAccess}
 * is a {@link HollowTypeReadState}.
 */
public interface HollowTypeDataAccess {

    /**
     * @return The {@link HollowDataAccess} for the dataset this type belongs to.
     */
    public HollowDataAccess getDataAccess();

    /**
     * @return the {@link HollowSchema} for this type.
     */
    public HollowSchema getSchema();

    public void setSamplingDirector(HollowSamplingDirector director);
    
    public void setFieldSpecificSamplingDirector(HollowFilterConfig fieldSpec, HollowSamplingDirector director);
    
    public void ignoreUpdateThreadForSampling(Thread t);

    public HollowSampler getSampler();

    /** Optional operation **/
    public HollowTypeReadState getTypeState();

}
