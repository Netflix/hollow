package com.netflix.vms.transformer.input;

import com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition;
import java.util.Map;

public class FollowVipPin {
    
    private final String transformerVip;
    private final Map<UpstreamDatasetDefinition.DatasetIdentifier, Long> inputVersions;
    private final long nowMillis;

    public FollowVipPin(String transformerVip, Map<UpstreamDatasetDefinition.DatasetIdentifier, Long> inputVersions, long nowMillis) {
        this.transformerVip = transformerVip;
        this.inputVersions = inputVersions;
        this.nowMillis = nowMillis;
    }
    
    public String getTransformerVip() {
        return transformerVip;
    }

    public Map<UpstreamDatasetDefinition.DatasetIdentifier, Long> getInputVersions() {
        return inputVersions;
    }

    public long getNowMillis() {
        return nowMillis;
    }
    
}
