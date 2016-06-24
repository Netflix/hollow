package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class DrmHeaderInfoHollow extends HollowObject {

    public DrmHeaderInfoHollow(DrmHeaderInfoDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getKeyId() {
        int refOrdinal = delegate().getKeyIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getDrmSystemId() {
        return delegate().getDrmSystemId(ordinal);
    }

    public Long _getDrmSystemIdBoxed() {
        return delegate().getDrmSystemIdBoxed(ordinal);
    }

    public StringHollow _getChecksum() {
        int refOrdinal = delegate().getChecksumOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public DrmHeaderInfoTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected DrmHeaderInfoDelegate delegate() {
        return (DrmHeaderInfoDelegate)delegate;
    }

}