package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface RatingsDelegate extends HollowObjectDelegate {

    public int getRatingCodeOrdinal(int ordinal);

    public long getRatingId(int ordinal);

    public Long getRatingIdBoxed(int ordinal);

    public int getDescriptionOrdinal(int ordinal);

    public RatingsTypeAPI getTypeAPI();

}