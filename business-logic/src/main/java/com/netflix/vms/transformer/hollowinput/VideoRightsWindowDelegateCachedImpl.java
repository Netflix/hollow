package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoRightsWindowDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoRightsWindowDelegate {

    private final int contractWindowStartDateOrdinal;
    private final Boolean onHold;
    private final int endDateOrdinal;
    private final int lastUpdateTsOrdinal;
    private final int contractIdsOrdinal;
    private final int contractWindowEndDateOrdinal;
    private final int startDateOrdinal;
   private VideoRightsWindowTypeAPI typeAPI;

    public VideoRightsWindowDelegateCachedImpl(VideoRightsWindowTypeAPI typeAPI, int ordinal) {
        this.contractWindowStartDateOrdinal = typeAPI.getContractWindowStartDateOrdinal(ordinal);
        this.onHold = typeAPI.getOnHoldBoxed(ordinal);
        this.endDateOrdinal = typeAPI.getEndDateOrdinal(ordinal);
        this.lastUpdateTsOrdinal = typeAPI.getLastUpdateTsOrdinal(ordinal);
        this.contractIdsOrdinal = typeAPI.getContractIdsOrdinal(ordinal);
        this.contractWindowEndDateOrdinal = typeAPI.getContractWindowEndDateOrdinal(ordinal);
        this.startDateOrdinal = typeAPI.getStartDateOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getContractWindowStartDateOrdinal(int ordinal) {
        return contractWindowStartDateOrdinal;
    }

    public boolean getOnHold(int ordinal) {
        return onHold.booleanValue();
    }

    public Boolean getOnHoldBoxed(int ordinal) {
        return onHold;
    }

    public int getEndDateOrdinal(int ordinal) {
        return endDateOrdinal;
    }

    public int getLastUpdateTsOrdinal(int ordinal) {
        return lastUpdateTsOrdinal;
    }

    public int getContractIdsOrdinal(int ordinal) {
        return contractIdsOrdinal;
    }

    public int getContractWindowEndDateOrdinal(int ordinal) {
        return contractWindowEndDateOrdinal;
    }

    public int getStartDateOrdinal(int ordinal) {
        return startDateOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoRightsWindowTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoRightsWindowTypeAPI) typeAPI;
    }

}