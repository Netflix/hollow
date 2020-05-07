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
package com.netflix.hollow.api.sampling;

import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HollowMapSampler implements HollowSampler {

    public static final HollowMapSampler NULL_SAMPLER = new HollowMapSampler("", DisabledSamplingDirector.INSTANCE);

    private final String typeName;

    private HollowSamplingDirector director;

    private long sizeSamples;
    private long getSamples;
    private long bucketRetrievalSamples;
    private long iteratorSamples;

    public HollowMapSampler(String typeName, HollowSamplingDirector director) {
        this.typeName = typeName;
        this.director = director;
    }

    public void setSamplingDirector(HollowSamplingDirector director) {
        if(!"".equals(typeName))
            this.director = director;
    }

    public void setFieldSpecificSamplingDirector(HollowFilterConfig fieldSpec, HollowSamplingDirector director) {
        if(!"".equals(typeName) && fieldSpec.doesIncludeType(typeName))
            this.director = director;
    }
    
    @Override
    public void setUpdateThread(Thread t) {
        director.setUpdateThread(t);
    }

    public void recordSize() {
        if(director.shouldRecord())
            sizeSamples++;
    }

    public void recordBucketRetrieval() {
        if(director.shouldRecord())
            bucketRetrievalSamples++;
    }

    public void recordGet() {
        if(director.shouldRecord())
            getSamples++;
    }

    public void recordIterator() {
        if(director.shouldRecord())
            iteratorSamples++;
    }

    @Override
    public boolean hasSampleResults() {
        return sizeSamples > 0 || getSamples > 0 || iteratorSamples > 0 || bucketRetrievalSamples > 0;
    }

    @Override
    public Collection<SampleResult> getSampleResults() {
        List<SampleResult> sampleResults = new ArrayList<SampleResult>(4);
        sampleResults.add(new SampleResult(typeName + ".size()", sizeSamples));
        sampleResults.add(new SampleResult(typeName + ".deserializeFrom()", getSamples));
        sampleResults.add(new SampleResult(typeName + ".iterator()", iteratorSamples));
        sampleResults.add(new SampleResult(typeName + ".bucketValue()", bucketRetrievalSamples));
        return sampleResults;
    }

    @Override
    public void reset() {
        sizeSamples = 0;
        getSamples = 0;
        iteratorSamples = 0;
        bucketRetrievalSamples = 0;
    }

}
