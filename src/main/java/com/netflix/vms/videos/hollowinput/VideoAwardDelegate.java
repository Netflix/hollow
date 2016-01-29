package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoAwardDelegate extends HollowObjectDelegate {

    public int getAwardOrdinal(int ordinal);

    public long getVideoId(int ordinal);

    public Long getVideoIdBoxed(int ordinal);

    public VideoAwardTypeAPI getTypeAPI();

}