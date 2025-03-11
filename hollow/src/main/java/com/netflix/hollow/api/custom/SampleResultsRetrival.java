package com.netflix.hollow.api.custom;

import com.netflix.hollow.api.sampling.SampleResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SampleResultsRetrival {

    public List<SampleResult> getAccessSampleResults(List<HollowTypeAPI> typeAPIs) {
        List<SampleResult> sampleResults = new ArrayList<SampleResult>();
        for(HollowTypeAPI typeAPI : typeAPIs) {
            sampleResults.addAll(typeAPI.getAccessSampleResults());
        }

        Collections.sort(sampleResults);

        return sampleResults;
    }

    public List<SampleResult> getBoxedSampleResults(List<HollowTypeAPI> typeAPIs) {
        List<SampleResult> sampleResults = new ArrayList<SampleResult>();
        for(HollowTypeAPI typeAPI : typeAPIs) {
            if(typeAPI instanceof HollowObjectTypeAPI) {
                sampleResults.addAll(((HollowObjectTypeAPI)typeAPI).getBoxedFieldAccessSampler().getSampleResults());
            }
        }

        Collections.sort(sampleResults);

        return sampleResults;
    }
}
