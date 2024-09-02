package com.netflix.hollow.core.read.engine;

public interface HollowTypeReadStateShard {

    AbstractHollowTypeDataElements getDataElements();

    int getShardOrdinalShift();
}
