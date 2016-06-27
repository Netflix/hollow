package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class DrmHeaderInfoTypeAPI extends HollowObjectTypeAPI {

    private final DrmHeaderInfoDelegateLookupImpl delegateLookupImpl;

    DrmHeaderInfoTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "keyId",
            "drmSystemId",
            "checksum"
        });
        this.delegateLookupImpl = new DrmHeaderInfoDelegateLookupImpl(this);
    }

    public int getKeyIdOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("DrmHeaderInfo", ordinal, "keyId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getKeyIdTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getDrmSystemId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("DrmHeaderInfo", ordinal, "drmSystemId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getDrmSystemIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("DrmHeaderInfo", ordinal, "drmSystemId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getChecksumOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("DrmHeaderInfo", ordinal, "checksum");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getChecksumTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public DrmHeaderInfoDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}