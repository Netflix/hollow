package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class CdnsTypeAPI extends HollowObjectTypeAPI {

    private final CdnsDelegateLookupImpl delegateLookupImpl;

    CdnsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "id",
            "name"
        });
        this.delegateLookupImpl = new CdnsDelegateLookupImpl(this);
    }

    public long getId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Cdns", ordinal, "id");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Cdns", ordinal, "id");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getNameOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Cdns", ordinal, "name");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getNameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public CdnsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}