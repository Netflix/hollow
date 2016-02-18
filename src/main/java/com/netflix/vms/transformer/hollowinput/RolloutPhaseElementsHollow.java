package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

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

    public RolloutPhaseCharacterListHollow _getCharacters() {
        int refOrdinal = delegate().getCharactersOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhaseCharacterListHollow(refOrdinal);
    }

    public RolloutPhaseCastListHollow _getCast() {
        int refOrdinal = delegate().getCastOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhaseCastListHollow(refOrdinal);
    }

    public RolloutPhaseNewArtworkHollow _getArtwork_new() {
        int refOrdinal = delegate().getArtwork_newOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhaseNewArtworkHollow(refOrdinal);
    }

    public RolloutPhaseOldArtworkListHollow _getArtwork() {
        int refOrdinal = delegate().getArtworkOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhaseOldArtworkListHollow(refOrdinal);
    }

    public RolloutPhaseTrailerListHollow _getTrailers() {
        int refOrdinal = delegate().getTrailersOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhaseTrailerListHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseElementsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseElementsDelegate delegate() {
        return (RolloutPhaseElementsDelegate)delegate;
    }

}