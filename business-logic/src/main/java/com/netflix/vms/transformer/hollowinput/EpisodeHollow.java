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