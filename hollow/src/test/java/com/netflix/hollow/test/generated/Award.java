package com.netflix.hollow.test.generated;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class Award extends HollowObject {

    public Award(AwardDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getId() {
        return delegate().getId(ordinal);
    }

    public Long getIdBoxed() {
        return delegate().getIdBoxed(ordinal);
    }

    public Movie getWinner() {
        int refOrdinal = delegate().getWinnerOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMovie(refOrdinal);
    }

    public SetOfMovie getNominees() {
        int refOrdinal = delegate().getNomineesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfMovie(refOrdinal);
    }

    public AwardsAPI api() {
        return typeApi().getAPI();
    }

    public AwardTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected AwardDelegate delegate() {
        return (AwardDelegate)delegate;
    }

}