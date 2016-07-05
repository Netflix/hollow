package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface RolloutPhaseElementsDelegate extends HollowObjectDelegate {

    public int getLocalized_metadataOrdinal(int ordinal);

    public int getArtworkOrdinal(int ordinal);

    public RolloutPhaseElementsTypeAPI getTypeAPI();

}