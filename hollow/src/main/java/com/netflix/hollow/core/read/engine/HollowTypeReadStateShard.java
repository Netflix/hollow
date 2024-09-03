package com.netflix.hollow.core.read.engine;

public interface HollowTypeReadStateShard {

    HollowTypeDataElements getDataElements();

    int getShardOrdinalShift();
}
