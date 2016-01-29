package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoRatingDelegate extends HollowObjectDelegate {

    public int getRatingOrdinal(int ordinal);

    public long getVideoId(int ordinal);

    public Long getVideoIdBoxed(int ordinal);

    public VideoRatingTypeAPI getTypeAPI();

}