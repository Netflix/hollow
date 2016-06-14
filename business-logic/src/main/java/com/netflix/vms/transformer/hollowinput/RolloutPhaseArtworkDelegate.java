package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface RolloutPhaseArtworkDelegate extends HollowObjectDelegate {

    public int getSourceFileIdsOrdinal(int ordinal);

    public RolloutPhaseArtworkTypeAPI getTypeAPI();

}