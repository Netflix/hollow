package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class Stories_SynopsesHollow extends HollowObject {

    public Stories_SynopsesHollow(Stories_SynopsesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public Stories_SynopsesNarrativeTextHollow _getNarrativeText() {
        int refOrdinal = delegate().getNarrativeTextOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStories_SynopsesNarrativeTextHollow(refOrdinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public Stories_SynopsesArrayOfHooksHollow _getHooks() {
        int refOrdinal = delegate().getHooksOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStories_SynopsesArrayOfHooksHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public Stories_SynopsesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected Stories_SynopsesDelegate delegate() {
        return (Stories_SynopsesDelegate)delegate;
    }

}