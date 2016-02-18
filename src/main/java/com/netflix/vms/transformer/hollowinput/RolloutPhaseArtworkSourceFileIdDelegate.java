package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface RolloutPhaseArtworkSourceFileIdDelegate extends HollowObjectDelegate {

    public int getValueOrdinal(int ordinal);

    public RolloutPhaseArtworkSourceFileIdTypeAPI getTypeAPI();

}