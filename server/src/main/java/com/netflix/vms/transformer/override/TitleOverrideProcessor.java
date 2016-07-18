package com.netflix.vms.transformer.override;

import com.netflix.hollow.read.engine.HollowReadStateEngine;

public interface TitleOverrideProcessor {

    public HollowReadStateEngine process(long dataVersion, int topNode) throws Throwable;

    public String getVip();
}