package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class StorageGroupsHollow extends HollowObject {

    public StorageGroupsHollow(StorageGroupsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getCdnId() {
        return delegate().getCdnId(ordinal);
    }

    public Long _getCdnIdBoxed() {
        return delegate().getCdnIdBoxed(ordinal);
    }

    public StringHollow _getId() {
        int refOrdinal = delegate().getIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StorageGroupsArrayOfCountriesHollow _getCountries() {
        int refOrdinal = delegate().getCountriesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStorageGroupsArrayOfCountriesHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public StorageGroupsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StorageGroupsDelegate delegate() {
        return (StorageGroupsDelegate)delegate;
    }

}