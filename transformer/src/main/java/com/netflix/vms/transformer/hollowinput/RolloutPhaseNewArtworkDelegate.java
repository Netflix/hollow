package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface RolloutPhaseNewArtworkDelegate extends HollowObjectDelegate {

    public int getSourceFileIdsOrdinal(int ordinal);

    public RolloutPhaseNewArtworkTypeAPI getTypeAPI();

}