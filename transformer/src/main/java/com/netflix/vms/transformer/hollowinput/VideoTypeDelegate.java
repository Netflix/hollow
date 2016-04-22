package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoTypeDelegate extends HollowObjectDelegate {

    public long getVideoId(int ordinal);

    public Long getVideoIdBoxed(int ordinal);

    public int getCountryInfosOrdinal(int ordinal);

    public VideoTypeTypeAPI getTypeAPI();

}