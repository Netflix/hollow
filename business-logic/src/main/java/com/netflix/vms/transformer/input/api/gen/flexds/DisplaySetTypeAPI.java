package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class DisplaySetTypeAPI extends HollowObjectTypeAPI {

    private final DisplaySetDelegateLookupImpl delegateLookupImpl;

    public DisplaySetTypeAPI(FlexDSAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "setId",
            "countryCodes",
            "isDefault",
            "displaySetTypes",
            "containers",
            "created",
            "updated"
        });
        this.delegateLookupImpl = new DisplaySetDelegateLookupImpl(this);
    }

    public long getSetId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("DisplaySet", ordinal, "setId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getSetIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("DisplaySet", ordinal, "setId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getCountryCodesOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("DisplaySet", ordinal, "countryCodes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public ListOfStringTypeAPI getCountryCodesTypeAPI() {
        return getAPI().getListOfStringTypeAPI();
    }

    public boolean getIsDefault(int ordinal) {
        if(fieldIndex[2] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("DisplaySet", ordinal, "isDefault"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]));
    }

    public Boolean getIsDefaultBoxed(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleBoolean("DisplaySet", ordinal, "isDefault");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]);
    }



    public int getDisplaySetTypesOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("DisplaySet", ordinal, "displaySetTypes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public SetOfStringTypeAPI getDisplaySetTypesTypeAPI() {
        return getAPI().getSetOfStringTypeAPI();
    }

    public int getContainersOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("DisplaySet", ordinal, "containers");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public SetOfContainerTypeAPI getContainersTypeAPI() {
        return getAPI().getSetOfContainerTypeAPI();
    }

    public int getCreatedOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("DisplaySet", ordinal, "created");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public AuditGroupTypeAPI getCreatedTypeAPI() {
        return getAPI().getAuditGroupTypeAPI();
    }

    public int getUpdatedOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("DisplaySet", ordinal, "updated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public AuditGroupTypeAPI getUpdatedTypeAPI() {
        return getAPI().getAuditGroupTypeAPI();
    }

    public DisplaySetDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public FlexDSAPI getAPI() {
        return (FlexDSAPI) api;
    }

}