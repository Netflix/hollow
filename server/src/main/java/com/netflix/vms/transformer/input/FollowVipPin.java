package com.netflix.vms.transformer.input;

public class FollowVipPin {
    
    private final String transformerVip;
    private final long inputVersionId;
    private final long gk2InputVersion;
    private final long nowMillis;
    
    public FollowVipPin(String transformerVip, long inputVersionId, long gk2InputVersion, long nowMillis) {
        this.transformerVip = transformerVip;
        this.inputVersionId = inputVersionId;
        this.gk2InputVersion = gk2InputVersion;
        this.nowMillis = nowMillis;
    }
    
    public String getTransformerVip() {
        return transformerVip;
    }

    public long getInputVersionId() {
        return inputVersionId;
    }

    public long getGk2InputVersion() {
        return gk2InputVersion;
    }

    public long getNowMillis() {
        return nowMillis;
    }
    
}
