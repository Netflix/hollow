package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoRightsWindowTypeAPI extends HollowObjectTypeAPI {

    private final VideoRightsWindowDelegateLookupImpl delegateLookupImpl;

    VideoRightsWindowTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "contractWindowStartDate",
            "onHold",
            "endDate",
            "lastUpdateTs",
            "contractIds",
            "contractWindowEndDate",
            "startDate"
        });
        this.delegateLookupImpl = new VideoRightsWindowDelegateLookupImpl(this);
    }

    public int getContractWindowStartDateOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsWindow", ordinal, "contractWindowStartDate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public DateTypeAPI getContractWindowStartDateTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public boolean getOnHold(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("VideoRightsWindow", ordinal, "onHold") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]) == Boolean.TRUE;
    }

    public Boolean getOnHoldBoxed(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("VideoRightsWindow", ordinal, "onHold");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]);
    }



    public int getEndDateOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsWindow", ordinal, "endDate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public DateTypeAPI getEndDateTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getLastUpdateTsOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsWindow", ordinal, "lastUpdateTs");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public DateTypeAPI getLastUpdateTsTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getContractIdsOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsWindow", ordinal, "contractIds");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public VideoRightsWindowContractIdListTypeAPI getContractIdsTypeAPI() {
        return getAPI().getVideoRightsWindowContractIdListTypeAPI();
    }

    public int getContractWindowEndDateOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsWindow", ordinal, "contractWindowEndDate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public DateTypeAPI getContractWindowEndDateTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getStartDateOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsWindow", ordinal, "startDate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public DateTypeAPI getStartDateTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public VideoRightsWindowDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}