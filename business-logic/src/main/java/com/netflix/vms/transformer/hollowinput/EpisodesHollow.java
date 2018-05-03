package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class EpisodesHollow extends HollowObject {

    public EpisodesHollow(EpisodesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public TranslatedTextHollow _getEpisodeName() {
        int refOrdinal = delegate().getEpisodeNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public long _getEpisodeId() {
        return delegate().getEpisodeId(ordinal);
    }

    public Long _getEpisodeIdBoxed() {
        return delegate().getEpisodeIdBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public EpisodesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected EpisodesDelegate delegate() {
        return (EpisodesDelegate)delegate;
    }

}