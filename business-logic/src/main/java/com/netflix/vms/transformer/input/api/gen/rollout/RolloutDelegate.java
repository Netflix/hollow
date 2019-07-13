package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RolloutDelegate extends HollowObjectDelegate {

    public long getRolloutId(int ordinal);

    public Long getRolloutIdBoxed(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public String getRolloutName(int ordinal);

    public boolean isRolloutNameEqual(int ordinal, String testValue);

    public int getRolloutNameOrdinal(int ordinal);

    public String getRolloutType(int ordinal);

    public boolean isRolloutTypeEqual(int ordinal, String testValue);

    public int getRolloutTypeOrdinal(int ordinal);

    public int getPhasesOrdinal(int ordinal);

    public RolloutTypeAPI getTypeAPI();

}