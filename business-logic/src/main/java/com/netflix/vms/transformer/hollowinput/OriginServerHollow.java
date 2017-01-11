package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class OriginServerHollow extends HollowObject {

    public OriginServerHollow(OriginServerDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getId() {
        return delegate().getId(ordinal);
    }

    public Long _getIdBoxed() {
        return delegate().getIdBoxed(ordinal);
    }

    public StringHollow _getName() {
        int refOrdinal = delegate().getNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getStorageGroupId() {
        int refOrdinal = delegate().getStorageGroupIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public OriginServerTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected OriginServerDelegate delegate() {
        return (OriginServerDelegate)delegate;
    }

}