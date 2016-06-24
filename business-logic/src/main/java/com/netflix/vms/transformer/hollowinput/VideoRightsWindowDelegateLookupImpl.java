package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoRightsWindowDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoRightsWindowDelegate {

    private final VideoRightsWindowTypeAPI typeAPI;

    public VideoRightsWindowDelegateLookupImpl(VideoRightsWindowTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getContractWindowStartDateOrdinal(int ordinal) {
        return typeAPI.getContractWindowStartDateOrdinal(ordinal);
    }

    public boolean getOnHold(int ordinal) {
        return typeAPI.getOnHold(ordinal);
    }

    public Boolean getOnHoldBoxed(int ordinal) {
        return typeAPI.getOnHoldBoxed(ordinal);
    }

    public int getEndDateOrdinal(int ordinal) {
        return typeAPI.getEndDateOrdinal(ordinal);
    }

    public int getLastUpdateTsOrdinal(int ordinal) {
        return typeAPI.getLastUpdateTsOrdinal(ordinal);
    }

    public int getContractIdsOrdinal(int ordinal) {
        return typeAPI.getContractIdsOrdinal(ordinal);
    }

    public int getContractWindowEndDateOrdinal(int ordinal) {
        return typeAPI.getContractWindowEndDateOrdinal(ordinal);
    }

    public int getStartDateOrdinal(int ordinal) {
        return typeAPI.getStartDateOrdinal(ordinal);
    }

    public VideoRightsWindowTypeAPI getTypeAPI() {
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