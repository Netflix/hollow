package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class TrailerTrailersHollow extends HollowObject {

    public TrailerTrailersHollow(TrailerTrailersDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public TrailerTrailersArrayOfThemesHollow _getThemes() {
        int refOrdinal = delegate().getThemesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTrailerTrailersArrayOfThemesHollow(refOrdinal);
    }

    public long _getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Long _getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public StringHollow _getIdentifier() {
        int refOrdinal = delegate().getIdentifierOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getPostPlay() {
        int refOrdinal = delegate().getPostPlayOrdinal(ordinal);
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

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public TrailerTrailersTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected TrailerTrailersDelegate delegate() {
        return (TrailerTrailersDelegate)delegate;
    }

}