package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class OriginServersTypeAPI extends HollowObjectTypeAPI {

    private final OriginServersDelegateLookupImpl delegateLookupImpl;

    OriginServersTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "id",
            "name",
            "storageGroupId"
        });
        this.delegateLookupImpl = new OriginServersDelegateLookupImpl(this);
    }

    public long getId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("OriginServers", ordinal, "id");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("OriginServers", ordinal, "id");
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
            return missingDataHandler().handleReferencedOrdinal("OriginServers", ordinal, "name");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getNameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getStorageGroupIdOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("OriginServers", ordinal, "storageGroupId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getStorageGroupIdTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public OriginServersDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}