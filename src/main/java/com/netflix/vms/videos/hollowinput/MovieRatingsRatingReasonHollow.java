package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class MovieRatingsRatingReasonHollow extends HollowObject {

    public MovieRatingsRatingReasonHollow(MovieRatingsRatingReasonDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public MovieRatingsRatingReasonMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMovieRatingsRatingReasonMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public MovieRatingsRatingReasonTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MovieRatingsRatingReasonDelegate delegate() {
        return (MovieRatingsRatingReasonDelegate)delegate;
    }

}