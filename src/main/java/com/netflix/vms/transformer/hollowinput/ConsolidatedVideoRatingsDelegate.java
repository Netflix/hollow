package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface ConsolidatedVideoRatingsDelegate extends HollowObjectDelegate {

    public int getRatingsOrdinal(int ordinal);

    public long getVideoId(int ordinal);

    public Long getVideoIdBoxed(int ordinal);

    public ConsolidatedVideoRatingsTypeAPI getTypeAPI();

}