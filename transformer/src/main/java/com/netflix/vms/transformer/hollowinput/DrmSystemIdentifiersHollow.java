package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class DrmSystemIdentifiersHollow extends HollowObject {

    public DrmSystemIdentifiersHollow(DrmSystemIdentifiersDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getId() {
        return delegate().getId(ordinal);
    }

    public Long _getIdBoxed() {
        return delegate().getIdBoxed(ordinal);
    }

    public StringHollow _getGuid() {
        int refOrdinal = delegate().getGuidOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getName() {
        int refOrdinal = delegate().getNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public boolean _getHeaderDataAvailable() {
        return delegate().getHeaderDataAvailable(ordinal);
    }

    public Boolean _getHeaderDataAvailableBoxed() {
        return delegate().getHeaderDataAvailableBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public DrmSystemIdentifiersTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected DrmSystemIdentifiersDelegate delegate() {
        return (DrmSystemIdentifiersDelegate)delegate;
    }

}