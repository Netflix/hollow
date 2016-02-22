package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class IndividualTrailerHollow extends HollowObject {

    public IndividualTrailerHollow(IndividualTrailerDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getIdentifier() {
        int refOrdinal = delegate().getIdentifierOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public long _getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Long _getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public StringHollow _getPostPlay() {
        int refOrdinal = delegate().getPostPlayOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getSubType() {
        int refOrdinal = delegate().getSubTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getAspectRatio() {
        int refOrdinal = delegate().getAspectRatioOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public ListOfStringHollow _getThemes() {
        int refOrdinal = delegate().getThemesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfStringHollow(refOrdinal);
    }

    public ListOfStringHollow _getUsages() {
        int refOrdinal = delegate().getUsagesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public IndividualTrailerTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected IndividualTrailerDelegate delegate() {
        return (IndividualTrailerDelegate)delegate;
    }

}