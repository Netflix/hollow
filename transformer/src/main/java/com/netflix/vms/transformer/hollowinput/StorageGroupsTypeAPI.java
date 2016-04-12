package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class StorageGroupsTypeAPI extends HollowObjectTypeAPI {

    private final StorageGroupsDelegateLookupImpl delegateLookupImpl;

    StorageGroupsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "id",
            "cdnId",
            "countries"
        });
        this.delegateLookupImpl = new StorageGroupsDelegateLookupImpl(this);
    }

    public int getIdOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("StorageGroups", ordinal, "id");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getIdTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getCdnId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("StorageGroups", ordinal, "cdnId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getCdnIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("StorageGroups", ordinal, "cdnId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getCountriesOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("StorageGroups", ordinal, "countries");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public ISOCountryListTypeAPI getCountriesTypeAPI() {
        return getAPI().getISOCountryListTypeAPI();
    }

    public StorageGroupsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}