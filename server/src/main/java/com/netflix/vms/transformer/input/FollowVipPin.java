package com.netflix.vms.transformer.input;

import java.util.Map;

public class FollowVipPin {
    
    private final Map<String, Long> inputVersions;
    private final long nowMillis;

    public FollowVipPin(Map<String, Long> inputVersions, long nowMillis) {
        this.inputVersions = inputVersions;
        this.nowMillis = nowMillis;
    }
    
    public Map<String, Long> getInputVersions() {
        return inputVersions;
    }

    public long getNowMillis() {
        return nowMillis;
    }
    
}
