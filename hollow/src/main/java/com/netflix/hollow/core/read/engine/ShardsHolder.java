package com.netflix.hollow.core.read.engine;

public abstract class ShardsHolder {

    public abstract HollowTypeReadStateShard[] getShards();

    public abstract int getShardNumberMask();

}
