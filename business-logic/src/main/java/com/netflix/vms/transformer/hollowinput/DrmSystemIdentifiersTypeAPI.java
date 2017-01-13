package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class DrmSystemIdentifiersTypeAPI extends HollowObjectTypeAPI {

    private final DrmSystemIdentifiersDelegateLookupImpl delegateLookupImpl;

    DrmSystemIdentifiersTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "id",
            "guid",
            "name",
            "headerDataAvailable"
        });
        this.delegateLookupImpl = new DrmSystemIdentifiersDelegateLookupImpl(this);
    }

    public long getId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("DrmSystemIdentifiers", ordinal, "id");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("DrmSystemIdentifiers", ordinal, "id");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getGuidOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("DrmSystemIdentifiers", ordinal, "guid");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getGuidTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getNameOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("DrmSystemIdentifiers", ordinal, "name");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getNameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public boolean getHeaderDataAvailable(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleBoolean("DrmSystemIdentifiers", ordinal, "headerDataAvailable") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]) == Boolean.TRUE;
    }

    public Boolean getHeaderDataAvailableBoxed(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleBoolean("DrmSystemIdentifiers", ordinal, "headerDataAvailable");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]);
    }



    public DrmSystemIdentifiersDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}