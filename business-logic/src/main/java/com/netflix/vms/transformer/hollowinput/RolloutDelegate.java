package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RolloutDelegate extends HollowObjectDelegate {

    public long getRolloutId(int ordinal);

    public Long getRolloutIdBoxed(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getRolloutNameOrdinal(int ordinal);

    public int getRolloutTypeOrdinal(int ordinal);

    public int getPhasesOrdinal(int ordinal);

    public RolloutTypeAPI getTypeAPI();

}