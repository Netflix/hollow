package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class StatusHollow extends HollowObject {

    public StatusHollow(StatusDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public StringHollow _getCountryCode() {
        int refOrdinal = delegate().getCountryCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public RightsHollow _getRights() {
        int refOrdinal = delegate().getRightsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRightsHollow(refOrdinal);
    }

    public FlagsHollow _getFlags() {
        int refOrdinal = delegate().getFlagsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getFlagsHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public StatusTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StatusDelegate delegate() {
        return (StatusDelegate)delegate;
    }

}