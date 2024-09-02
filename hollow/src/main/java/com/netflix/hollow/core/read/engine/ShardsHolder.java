package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElementsJoiner;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElementsSplitter;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import java.util.Arrays;

public abstract class ShardsHolder {

    public abstract HollowTypeReadStateShard[] getShards();

    public abstract int getShardNumberMask();



}
