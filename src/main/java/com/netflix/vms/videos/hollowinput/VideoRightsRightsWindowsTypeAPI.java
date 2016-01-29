package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoRightsRightsWindowsTypeAPI extends HollowObjectTypeAPI {

    private final VideoRightsRightsWindowsDelegateLookupImpl delegateLookupImpl;

    VideoRightsRightsWindowsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "contractWindowStartDate",
            "onHold",
            "endDate",
            "lastUpdateTs",
            "contractIds",
            "contractWindowEndDate",
            "startDate"
        });
        this.delegateLookupImpl = new VideoRightsRightsWindowsDelegateLookupImpl(this);
    }

    public long getContractWindowStartDate(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("VideoRightsRightsWindows", ordinal, "contractWindowStartDate");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getContractWindowStartDateBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("VideoRightsRightsWindows", ordinal, "contractWindowStartDate");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public boolean getOnHold(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("VideoRightsRightsWindows", ordinal, "onHold") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]) == Boolean.TRUE;
    }

    public Boolean getOnHoldBoxed(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("VideoRightsRightsWindows", ordinal, "onHold");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]);
    }



    public long getEndDate(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("VideoRightsRightsWindows", ordinal, "endDate");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getEndDateBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("VideoRightsRightsWindows", ordinal, "endDate");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getLastUpdateTs(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("VideoRightsRightsWindows", ordinal, "lastUpdateTs");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getLastUpdateTsBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("VideoRightsRightsWindows", ordinal, "lastUpdateTs");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getContractIdsOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsRightsWindows", ordinal, "contractIds");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public VideoRightsRightsWindowsArrayOfContractIdsTypeAPI getContractIdsTypeAPI() {
        return getAPI().getVideoRightsRightsWindowsArrayOfContractIdsTypeAPI();
    }

    public long getContractWindowEndDate(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleLong("VideoRightsRightsWindows", ordinal, "contractWindowEndDate");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[5]);
    }

    public Long getContractWindowEndDateBoxed(int ordinal) {
        long l;
        if(fieldIndex[5] == -1) {
            l = missingDataHandler().handleLong("VideoRightsRightsWindows", ordinal, "contractWindowEndDate");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[5]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[5]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getStartDate(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleLong("VideoRightsRightsWindows", ordinal, "startDate");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[6]);
    }

    public Long getStartDateBoxed(int ordinal) {
        long l;
        if(fieldIndex[6] == -1) {
            l = missingDataHandler().handleLong("VideoRightsRightsWindows", ordinal, "startDate");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[6]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[6]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public VideoRightsRightsWindowsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}