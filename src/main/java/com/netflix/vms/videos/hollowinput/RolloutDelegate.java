package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface RolloutDelegate extends HollowObjectDelegate {

    public int getRolloutNameOrdinal(int ordinal);

    public int getLaunchDatesOrdinal(int ordinal);

    public long getRolloutId(int ordinal);

    public Long getRolloutIdBoxed(int ordinal);

    public int getRolloutTypeOrdinal(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getPhasesOrdinal(int ordinal);

    public RolloutTypeAPI getTypeAPI();

}