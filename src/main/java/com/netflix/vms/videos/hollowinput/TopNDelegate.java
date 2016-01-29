package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface TopNDelegate extends HollowObjectDelegate {

    public long getVideoId(int ordinal);

    public Long getVideoIdBoxed(int ordinal);

    public int getAttributesOrdinal(int ordinal);

    public int getDseSourceFileOrdinal(int ordinal);

    public TopNTypeAPI getTypeAPI();

}