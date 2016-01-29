package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class DrmSystemIdentifiersTypeAPI extends HollowObjectTypeAPI {

    private final DrmSystemIdentifiersDelegateLookupImpl delegateLookupImpl;

    DrmSystemIdentifiersTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "name",
            "guid",
            "headerDataAvailable",
            "id"
        });
        this.delegateLookupImpl = new DrmSystemIdentifiersDelegateLookupImpl(this);
    }

    public int getNameOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("DrmSystemIdentifiers", ordinal, "name");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getNameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getGuidOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("DrmSystemIdentifiers", ordinal, "guid");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getGuidTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public boolean getHeaderDataAvailable(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleBoolean("DrmSystemIdentifiers", ordinal, "headerDataAvailable") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]) == Boolean.TRUE;
    }

    public Boolean getHeaderDataAvailableBoxed(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleBoolean("DrmSystemIdentifiers", ordinal, "headerDataAvailable");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]);
    }



    public long getId(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("DrmSystemIdentifiers", ordinal, "id");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("DrmSystemIdentifiers", ordinal, "id");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public DrmSystemIdentifiersDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}