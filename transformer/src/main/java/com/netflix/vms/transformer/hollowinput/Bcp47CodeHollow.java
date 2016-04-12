package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class Bcp47CodeHollow extends HollowObject {

    public Bcp47CodeHollow(Bcp47CodeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getLanguageId() {
        return delegate().getLanguageId(ordinal);
    }

    public Long _getLanguageIdBoxed() {
        return delegate().getLanguageIdBoxed(ordinal);
    }

    public StringHollow _getIso6392Code() {
        int refOrdinal = delegate().getIso6392CodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getBcp47Code() {
        int refOrdinal = delegate().getBcp47CodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getIso6391Code() {
        int refOrdinal = delegate().getIso6391CodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getIso6393Code() {
        int refOrdinal = delegate().getIso6393CodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public Bcp47CodeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected Bcp47CodeDelegate delegate() {
        return (Bcp47CodeDelegate)delegate;
    }

}