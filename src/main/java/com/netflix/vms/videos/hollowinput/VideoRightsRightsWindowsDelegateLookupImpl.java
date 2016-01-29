package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoRightsRightsWindowsDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoRightsRightsWindowsDelegate {

    private final VideoRightsRightsWindowsTypeAPI typeAPI;

    public VideoRightsRightsWindowsDelegateLookupImpl(VideoRightsRightsWindowsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getContractWindowStartDate(int ordinal) {
        return typeAPI.getContractWindowStartDate(ordinal);
    }

    public Long getContractWindowStartDateBoxed(int ordinal) {
        return typeAPI.getContractWindowStartDateBoxed(ordinal);
    }

    public boolean getOnHold(int ordinal) {
        return typeAPI.getOnHold(ordinal);
    }

    public Boolean getOnHoldBoxed(int ordinal) {
        return typeAPI.getOnHoldBoxed(ordinal);
    }

    public long getEndDate(int ordinal) {
        return typeAPI.getEndDate(ordinal);
    }

    public Long getEndDateBoxed(int ordinal) {
        return typeAPI.getEndDateBoxed(ordinal);
    }

    public long getLastUpdateTs(int ordinal) {
        return typeAPI.getLastUpdateTs(ordinal);
    }

    public Long getLastUpdateTsBoxed(int ordinal) {
        return typeAPI.getLastUpdateTsBoxed(ordinal);
    }

    public int getContractIdsOrdinal(int ordinal) {
        return typeAPI.getContractIdsOrdinal(ordinal);
    }

    public long getContractWindowEndDate(int ordinal) {
        return typeAPI.getContractWindowEndDate(ordinal);
    }

    public Long getContractWindowEndDateBoxed(int ordinal) {
        return typeAPI.getContractWindowEndDateBoxed(ordinal);
    }

    public long getStartDate(int ordinal) {
        return typeAPI.getStartDate(ordinal);
    }

    public Long getStartDateBoxed(int ordinal) {
        return typeAPI.getStartDateBoxed(ordinal);
    }

    public VideoRightsRightsWindowsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}