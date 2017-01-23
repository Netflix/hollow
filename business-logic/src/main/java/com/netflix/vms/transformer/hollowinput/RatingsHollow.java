package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RatingsHollow extends HollowObject {

    public RatingsHollow(RatingsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getRatingId() {
        return delegate().getRatingId(ordinal);
    }

    public Long _getRatingIdBoxed() {
        return delegate().getRatingIdBoxed(ordinal);
    }

    public TranslatedTextHollow _getRatingCode() {
        int refOrdinal = delegate().getRatingCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public TranslatedTextHollow _getDescription() {
        int refOrdinal = delegate().getDescriptionOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public RatingsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RatingsDelegate delegate() {
        return (RatingsDelegate)delegate;
    }

}