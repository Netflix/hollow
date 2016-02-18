package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

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

    public EpisodeListHollow _getChildren() {
        int refOrdinal = delegate().getChildrenOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getEpisodeListHollow(refOrdinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public SeasonTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected SeasonDelegate delegate() {
        return (SeasonDelegate)delegate;
    }

}