package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhasesElementsHollow extends HollowObject {

    public RolloutPhasesElementsHollow(RolloutPhasesElementsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public RolloutPhasesElementsLocalized_metadataHollow _getLocalized_metadata() {
        int refOrdinal = delegate().getLocalized_metadataOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhasesElementsLocalized_metadataHollow(refOrdinal);
    }

    public RolloutPhasesElementsArrayOfCharactersHollow _getCharacters() {
        int refOrdinal = delegate().getCharactersOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhasesElementsArrayOfCharactersHollow(refOrdinal);
    }

    public RolloutPhasesElementsArrayOfCastHollow _getCast() {
        int refOrdinal = delegate().getCastOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhasesElementsArrayOfCastHollow(refOrdinal);
    }

    public RolloutPhasesElementsArtwork_newHollow _getArtwork_new() {
        int refOrdinal = delegate().getArtwork_newOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhasesElementsArtwork_newHollow(refOrdinal);
    }

    public RolloutPhasesElementsArrayOfArtworkHollow _getArtwork() {
        int refOrdinal = delegate().getArtworkOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhasesElementsArrayOfArtworkHollow(refOrdinal);
    }

    public RolloutPhasesElementsArrayOfTrailersHollow _getTrailers() {
        int refOrdinal = delegate().getTrailersOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhasesElementsArrayOfTrailersHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhasesElementsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhasesElementsDelegate delegate() {
        return (RolloutPhasesElementsDelegate)delegate;
    }

}