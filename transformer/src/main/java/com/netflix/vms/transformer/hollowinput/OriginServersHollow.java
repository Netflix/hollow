package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class OriginServersHollow extends HollowObject {

    public OriginServersHollow(OriginServersDelegate delegate, int ordinal) {
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

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public OriginServersTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected OriginServersDelegate delegate() {
        return (OriginServersDelegate)delegate;
    }

}