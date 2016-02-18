package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoAwardAwardDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoAwardAwardDelegate {

    private final VideoAwardAwardTypeAPI typeAPI;

    public VideoAwardAwardDelegateLookupImpl(VideoAwardAwardTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getAwardId(int ordinal) {
        return typeAPI.getAwardId(ordinal);
    }

    public Long getAwardIdBoxed(int ordinal) {
        return typeAPI.getAwardIdBoxed(ordinal);
    }

    public long getSequenceNumber(int ordinal) {
        return typeAPI.getSequenceNumber(ordinal);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return typeAPI.getSequenceNumberBoxed(ordinal);
    }

    public boolean getWinner(int ordinal) {
        return typeAPI.getWinner(ordinal);
    }

    public Boolean getWinnerBoxed(int ordinal) {
        return typeAPI.getWinnerBoxed(ordinal);
    }

    public long getYear(int ordinal) {
        return typeAPI.getYear(ordinal);
    }

    public Long getYearBoxed(int ordinal) {
        return typeAPI.getYearBoxed(ordinal);
    }

    public long getPersonId(int ordinal) {
        return typeAPI.getPersonId(ordinal);
    }

    public Long getPersonIdBoxed(int ordinal) {
        return typeAPI.getPersonIdBoxed(ordinal);
    }

    public VideoAwardAwardTypeAPI getTypeAPI() {
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