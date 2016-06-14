package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class MovieRatingsHollow extends HollowObject {

    public MovieRatingsHollow(MovieRatingsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public StringHollow _getMedia() {
        int refOrdinal = delegate().getMediaOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getCertificationTypeId() {
        return delegate().getCertificationTypeId(ordinal);
    }

    public Long _getCertificationTypeIdBoxed() {
        return delegate().getCertificationTypeIdBoxed(ordinal);
    }

    public TranslatedTextHollow _getRatingReason() {
        int refOrdinal = delegate().getRatingReasonOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public MovieRatingsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MovieRatingsDelegate delegate() {
        return (MovieRatingsDelegate)delegate;
    }

}