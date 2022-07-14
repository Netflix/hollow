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
package com.netflix.hollow.api.custom;

import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.api.sampling.SampleResult;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import java.util.Collection;

/**
 *  A Hollow Type API provides methods for accessing data in Hollow records without creating
 *  wrapper objects as handles.  Instead, the ordinals can be used directly as handles to the data.
 *  <p>
 *  This can be useful in tight loops, where the excess object creation incurred by using a Generated or Generic
 *  Hollow Object API would be prohibitively expensive.
 */
public abstract class HollowTypeAPI {

    protected final HollowAPI api;
    protected final HollowTypeDataAccess typeDataAccess;

    protected HollowTypeAPI(HollowAPI api, HollowTypeDataAccess typeDataAccess) {
        this.api = api;
        this.typeDataAccess = typeDataAccess;
    }

    public HollowAPI getAPI() {
        return api;
    }

    public HollowTypeDataAccess getTypeDataAccess() {
        return typeDataAccess;
    }

    public void setSamplingDirector(HollowSamplingDirector samplingDirector) {
        typeDataAccess.setSamplingDirector(samplingDirector);
    }

    public void setFieldSpecificSamplingDirector(HollowFilterConfig fieldSpec, HollowSamplingDirector director) {
        typeDataAccess.setFieldSpecificSamplingDirector(fieldSpec, director);
    }

    public void ignoreUpdateThreadForSampling(Thread t) {
        typeDataAccess.ignoreUpdateThreadForSampling(t);
    }

    public Collection<SampleResult> getAccessSampleResults() {
        return typeDataAccess.getSampler().getSampleResults();
    }

}
