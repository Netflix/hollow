package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RolloutPhaseArtworkDelegate extends HollowObjectDelegate {

    public int getSourceFileIdsOrdinal(int ordinal);

    public RolloutPhaseArtworkTypeAPI getTypeAPI();

}