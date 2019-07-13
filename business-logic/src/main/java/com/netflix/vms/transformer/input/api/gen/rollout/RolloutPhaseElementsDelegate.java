package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RolloutPhaseElementsDelegate extends HollowObjectDelegate {

    public int getLocalized_metadataOrdinal(int ordinal);

    public int getArtworkOrdinal(int ordinal);

    public RolloutPhaseElementsTypeAPI getTypeAPI();

}