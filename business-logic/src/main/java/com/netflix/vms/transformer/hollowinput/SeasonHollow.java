package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class SeasonHollow extends HollowObject {

    public SeasonHollow(SeasonDelegate delegate, int ordinal) {
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

    public EpisodeListHollow _getEpisodes() {
        int refOrdinal = delegate().getEpisodesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getEpisodeListHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public SeasonTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected SeasonDelegate delegate() {
        return (SeasonDelegate)delegate;
    }

}