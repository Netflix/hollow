package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class RolloutPhaseElementsHollow extends HollowObject {

    public RolloutPhaseElementsHollow(RolloutPhaseElementsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public RolloutPhaseLocalizedMetadataHollow _getLocalized_metadata() {
        int refOrdinal = delegate().getLocalized_metadataOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhaseLocalizedMetadataHollow(refOrdinal);
    }

    public RolloutPhaseArtworkHollow _getArtwork() {
        int refOrdinal = delegate().getArtworkOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhaseArtworkHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseElementsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseElementsDelegate delegate() {
        return (RolloutPhaseElementsDelegate)delegate;
    }

}