package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class RolloutPhaseElements extends HollowObject {

    public RolloutPhaseElements(RolloutPhaseElementsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public RolloutPhaseLocalizedMetadata getLocalized_metadata() {
        int refOrdinal = delegate().getLocalized_metadataOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhaseLocalizedMetadata(refOrdinal);
    }

    public RolloutPhaseArtwork getArtwork() {
        int refOrdinal = delegate().getArtworkOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhaseArtwork(refOrdinal);
    }

    public RolloutAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseElementsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseElementsDelegate delegate() {
        return (RolloutPhaseElementsDelegate)delegate;
    }

}