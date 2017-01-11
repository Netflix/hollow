package com.netflix.vms.transformer.override;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public interface PinTitleProcessor {

    public HollowReadStateEngine process(long dataVersion, int... topNodes) throws Throwable;

    public String getVip();
}