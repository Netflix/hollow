package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StoriesSynopsesHollow extends HollowObject {

    public StoriesSynopsesHollow(StoriesSynopsesDelegate delegate, int ordinal) {
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

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public StoriesSynopsesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StoriesSynopsesDelegate delegate() {
        return (StoriesSynopsesDelegate)delegate;
    }

}