package com.netflix.vms.transformer.input;

public class FollowVipPin {
    
    private final String transformerVip;
    private final long inputVersionId;
    private final long nowMillis;
    
    public FollowVipPin(String transformerVip, long inputVersionId, long nowMillis) {
        this.transformerVip = transformerVip;
        this.inputVersionId = inputVersionId;
        this.nowMillis = nowMillis;
    }
    
    public String getTransformerVip() {
        return transformerVip;
    }

    public long getInputVersionId() {
        return inputVersionId;
    }

    public long getNowMillis() {
        return nowMillis;
    }
    
}
