package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RatingsDelegate extends HollowObjectDelegate {

    public long getRatingId(int ordinal);

    public Long getRatingIdBoxed(int ordinal);

    public int getRatingCodeOrdinal(int ordinal);

    public int getDescriptionOrdinal(int ordinal);

    public RatingsTypeAPI getTypeAPI();

}