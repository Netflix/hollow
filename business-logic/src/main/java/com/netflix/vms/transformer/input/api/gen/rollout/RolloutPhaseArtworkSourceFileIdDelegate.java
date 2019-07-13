package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RolloutPhaseArtworkSourceFileIdDelegate extends HollowObjectDelegate {

    public String getValue(int ordinal);

    public boolean isValueEqual(int ordinal, String testValue);

    public int getValueOrdinal(int ordinal);

    public RolloutPhaseArtworkSourceFileIdTypeAPI getTypeAPI();

}