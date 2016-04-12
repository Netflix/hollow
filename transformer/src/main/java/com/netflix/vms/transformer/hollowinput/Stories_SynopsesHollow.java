package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class Stories_SynopsesHollow extends HollowObject {

    public Stories_SynopsesHollow(Stories_SynopsesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public TranslatedTextHollow _getNarrativeText() {
        int refOrdinal = delegate().getNarrativeTextOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public StoriesSynopsesHookListHollow _getHooks() {
        int refOrdinal = delegate().getHooksOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStoriesSynopsesHookListHollow(refOrdinal);
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