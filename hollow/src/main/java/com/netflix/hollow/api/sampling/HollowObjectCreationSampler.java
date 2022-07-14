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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class HollowObjectCreationSampler implements HollowSampler {

    private final String typeNames[];
    private final long creationSamples[];
    private final HollowSamplingDirector typeDirectors[];

    public HollowObjectCreationSampler(String... typeNames) {
        this.typeNames = typeNames;
        this.creationSamples = new long[typeNames.length];

        HollowSamplingDirector[] typeDirectors = new HollowSamplingDirector[typeNames.length];
        Arrays.fill(typeDirectors, DisabledSamplingDirector.INSTANCE);

        this.typeDirectors = typeDirectors;
    }

    public void recordCreation(int index) {
        if(typeDirectors[index].shouldRecord())
            creationSamples[index]++;
    }

    @Override
    public void setSamplingDirector(HollowSamplingDirector director) {
        Arrays.fill(typeDirectors, director);
    }

    @Override
    public void setFieldSpecificSamplingDirector(HollowFilterConfig fieldSpec, HollowSamplingDirector director) {
        for(int i = 0; i < typeNames.length; i++) {
            if(fieldSpec.doesIncludeType(typeNames[i]))
                typeDirectors[i] = director;
        }
    }

    @Override
    public void setUpdateThread(Thread t) {
        for(int i = 0; i < typeDirectors.length; i++)
            typeDirectors[i].setUpdateThread(t);
    }

    @Override
    public boolean hasSampleResults() {
        for(int i = 0; i < creationSamples.length; i++)
            if(creationSamples[i] > 0)
                return true;
        return false;
    }

    @Override
    public Collection<SampleResult> getSampleResults() {
        List<SampleResult> results = new ArrayList<SampleResult>(typeNames.length);

        for(int i = 0; i < typeNames.length; i++) {
            results.add(new SampleResult(typeNames[i], creationSamples[i]));
        }

        Collections.sort(results);

        return results;
    }

    @Override
    public void reset() {
        Arrays.fill(creationSamples, 0L);
    }
}
