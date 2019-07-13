package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RolloutPhaseArtworkDelegate extends HollowObjectDelegate {

    public int getSourceFileIdsOrdinal(int ordinal);

    public RolloutPhaseArtworkTypeAPI getTypeAPI();

}