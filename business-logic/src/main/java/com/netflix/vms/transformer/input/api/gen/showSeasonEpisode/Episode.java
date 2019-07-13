package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class Episode extends HollowObject {

    public Episode(EpisodeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public int getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Integer getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public long getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public boolean getMidSeason() {
        return delegate().getMidSeason(ordinal);
    }

    public Boolean getMidSeasonBoxed() {
        return delegate().getMidSeasonBoxed(ordinal);
    }

    public boolean getSeasonFinale() {
        return delegate().getSeasonFinale(ordinal);
    }

    public Boolean getSeasonFinaleBoxed() {
        return delegate().getSeasonFinaleBoxed(ordinal);
    }

    public boolean getShowFinale() {
        return delegate().getShowFinale(ordinal);
    }

    public Boolean getShowFinaleBoxed() {
        return delegate().getShowFinaleBoxed(ordinal);
    }

    public ShowSeasonEpisodeAPI api() {
        return typeApi().getAPI();
    }

    public EpisodeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected EpisodeDelegate delegate() {
        return (EpisodeDelegate)delegate;
    }

}