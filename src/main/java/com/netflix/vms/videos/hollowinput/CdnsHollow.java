package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CdnsHollow extends HollowObject {

    public CdnsHollow(CdnsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getName() {
        int refOrdinal = delegate().getNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getId() {
        return delegate().getId(ordinal);
    }

    public Long _getIdBoxed() {
        return delegate().getIdBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CdnsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CdnsDelegate delegate() {
        return (CdnsDelegate)delegate;
    }

}