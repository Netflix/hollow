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

import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.api.sampling.SampleResult;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A HollowAPI wraps a HollowDataAccess.  This is the parent class of any Generated Hollow API.
 * 
 * Generated Hollow APIs are created via the {@link HollowAPIGenerator}.
 */
public class HollowAPI {

    private final HollowDataAccess dataAccess;
    private final List<HollowTypeAPI> typeAPIs;

    protected HollowSamplingDirector samplingDirector;

    public HollowAPI(HollowDataAccess dataAccess) {
        this.dataAccess = dataAccess;
        this.typeAPIs = new ArrayList<HollowTypeAPI>();
    }

    public HollowDataAccess getDataAccess() {
        return dataAccess;
    }

    public HollowSamplingDirector getSamplingDirector() {
        return samplingDirector;
    }

    public void setSamplingDirector(HollowSamplingDirector samplingDirector) {
        this.samplingDirector = samplingDirector;
        for(HollowTypeAPI typeAPI : typeAPIs) {
            typeAPI.setSamplingDirector(samplingDirector);
        }
    }

    public void setFieldSpecificSamplingDirector(HollowFilterConfig fieldSpec, HollowSamplingDirector director) {
        for(HollowTypeAPI typeAPI : typeAPIs) {
            typeAPI.setFieldSpecificSamplingDirector(fieldSpec, director);
        }
    }

    public void ignoreUpdateThreadForSampling(Thread t) {
        for(HollowTypeAPI typeAPI : typeAPIs) {
            typeAPI.ignoreUpdateThreadForSampling(t);
        }
    }

    public List<SampleResult> getAccessSampleResults() {
        List<SampleResult> sampleResults = new ArrayList<SampleResult>();
        for(HollowTypeAPI typeAPI : typeAPIs) {
            sampleResults.addAll(typeAPI.getAccessSampleResults());
        }

        Collections.sort(sampleResults);

        return sampleResults;
    }

    public List<SampleResult> getBoxedSampleResults() {
        List<SampleResult> sampleResults = new ArrayList<SampleResult>();
        for(HollowTypeAPI typeAPI : typeAPIs) {
            if(typeAPI instanceof HollowObjectTypeAPI) {
                sampleResults.addAll(((HollowObjectTypeAPI) typeAPI).getBoxedFieldAccessSampler().getSampleResults());
            }
        }

        Collections.sort(sampleResults);

        return sampleResults;
    }

    public void detachCaches() {
    }

    protected void addTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPIs.add(typeAPI);
    }

}
