package com.netflix.vms.transformer.input;

import com.netflix.vms.transformer.common.input.UpstreamDatasetHolder;
import java.util.Map;

public class FollowVipPin {
    
    private final String transformerVip;
    private final Map<UpstreamDatasetHolder.Dataset, Long> inputVersions;
    private final long nowMillis;

    public FollowVipPin(String transformerVip, Map<UpstreamDatasetHolder.Dataset, Long> inputVersions, long nowMillis) {
        this.transformerVip = transformerVip;
        this.inputVersions = inputVersions;
        this.nowMillis = nowMillis;
    }
    
    public String getTransformerVip() {
        return transformerVip;
    }

    public Map<UpstreamDatasetHolder.Dataset, Long> getInputVersions() {
        return inputVersions;
    }

    public long getNowMillis() {
        return nowMillis;
    }
    
}
