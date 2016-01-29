package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface MapKeyDelegate extends HollowObjectDelegate {

    public String getKey(int ordinal);

    public boolean isKeyEqual(int ordinal, String testValue);

    public MapKeyTypeAPI getTypeAPI();

}