package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class EpisodeHollow extends HollowObject {

    public EpisodeHollow(EpisodeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Long _getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public boolean _getMidSeason() {
        return delegate().getMidSeason(ordinal);
    }

    public Boolean _getMidSeasonBoxed() {
        return delegate().getMidSeasonBoxed(ordinal);
    }

    public boolean _getSeasonFinale() {
        return delegate().getSeasonFinale(ordinal);
    }

    public Boolean _getSeasonFinaleBoxed() {
        return delegate().getSeasonFinaleBoxed(ordinal);
    }

    public boolean _getShowFinale() {
        return delegate().getShowFinale(ordinal);
    }

    public Boolean _getShowFinaleBoxed() {
        return delegate().getShowFinaleBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public EpisodeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected EpisodeDelegate delegate() {
        return (EpisodeDelegate)delegate;
    }

}