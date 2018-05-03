package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class StorageGroupsHollow extends HollowObject {

    public StorageGroupsHollow(StorageGroupsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getId() {
        int refOrdinal = delegate().getIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getCdnId() {
        return delegate().getCdnId(ordinal);
    }

    public Long _getCdnIdBoxed() {
        return delegate().getCdnIdBoxed(ordinal);
    }

    public ISOCountryListHollow _getCountries() {
        int refOrdinal = delegate().getCountriesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getISOCountryListHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public StorageGroupsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StorageGroupsDelegate delegate() {
        return (StorageGroupsDelegate)delegate;
    }

}