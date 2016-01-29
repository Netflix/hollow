package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoRightsRightsWindowsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoRightsRightsWindowsDelegate {

    private final Long contractWindowStartDate;
    private final Boolean onHold;
    private final Long endDate;
    private final Long lastUpdateTs;
    private final int contractIdsOrdinal;
    private final Long contractWindowEndDate;
    private final Long startDate;
   private VideoRightsRightsWindowsTypeAPI typeAPI;

    public VideoRightsRightsWindowsDelegateCachedImpl(VideoRightsRightsWindowsTypeAPI typeAPI, int ordinal) {
        this.contractWindowStartDate = typeAPI.getContractWindowStartDateBoxed(ordinal);
        this.onHold = typeAPI.getOnHoldBoxed(ordinal);
        this.endDate = typeAPI.getEndDateBoxed(ordinal);
        this.lastUpdateTs = typeAPI.getLastUpdateTsBoxed(ordinal);
        this.contractIdsOrdinal = typeAPI.getContractIdsOrdinal(ordinal);
        this.contractWindowEndDate = typeAPI.getContractWindowEndDateBoxed(ordinal);
        this.startDate = typeAPI.getStartDateBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getContractWindowStartDate(int ordinal) {
        return contractWindowStartDate.longValue();
    }

    public Long getContractWindowStartDateBoxed(int ordinal) {
        return contractWindowStartDate;
    }

    public boolean getOnHold(int ordinal) {
        return onHold.booleanValue();
    }

    public Boolean getOnHoldBoxed(int ordinal) {
        return onHold;
    }

    public long getEndDate(int ordinal) {
        return endDate.longValue();
    }

    public Long getEndDateBoxed(int ordinal) {
        return endDate;
    }

    public long getLastUpdateTs(int ordinal) {
        return lastUpdateTs.longValue();
    }

    public Long getLastUpdateTsBoxed(int ordinal) {
        return lastUpdateTs;
    }

    public int getContractIdsOrdinal(int ordinal) {
        return contractIdsOrdinal;
    }

    public long getContractWindowEndDate(int ordinal) {
        return contractWindowEndDate.longValue();
    }

    public Long getContractWindowEndDateBoxed(int ordinal) {
        return contractWindowEndDate;
    }

    public long getStartDate(int ordinal) {
        return startDate.longValue();
    }

    public Long getStartDateBoxed(int ordinal) {
        return startDate;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoRightsRightsWindowsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoRightsRightsWindowsTypeAPI) typeAPI;
    }

}