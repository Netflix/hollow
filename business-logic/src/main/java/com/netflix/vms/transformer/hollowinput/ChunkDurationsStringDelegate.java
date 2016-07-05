package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface ChunkDurationsStringDelegate extends HollowObjectDelegate {

    public String getValue(int ordinal);

    public boolean isValueEqual(int ordinal, String testValue);

    public ChunkDurationsStringTypeAPI getTypeAPI();

}