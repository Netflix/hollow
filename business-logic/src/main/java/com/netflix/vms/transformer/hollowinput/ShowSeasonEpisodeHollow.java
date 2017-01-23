package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ShowSeasonEpisodeHollow extends HollowObject {

    public ShowSeasonEpisodeHollow(ShowSeasonEpisodeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public long _getDisplaySetId() {
        return delegate().getDisplaySetId(ordinal);
    }

    public Long _getDisplaySetIdBoxed() {
        return delegate().getDisplaySetIdBoxed(ordinal);
    }

    public ISOCountryListHollow _getCountryCodes() {
        int refOrdinal = delegate().getCountryCodesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getISOCountryListHollow(refOrdinal);
    }

    public SeasonListHollow _getSeasons() {
        int refOrdinal = delegate().getSeasonsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSeasonListHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ShowSeasonEpisodeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ShowSeasonEpisodeDelegate delegate() {
        return (ShowSeasonEpisodeDelegate)delegate;
    }

}