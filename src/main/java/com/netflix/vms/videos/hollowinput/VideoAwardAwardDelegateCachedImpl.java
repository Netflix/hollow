package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoAwardAwardDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoAwardAwardDelegate {

    private final Long awardId;
    private final Long sequenceNumber;
    private final Boolean winner;
    private final Long year;
    private final Long personId;
   private VideoAwardAwardTypeAPI typeAPI;

    public VideoAwardAwardDelegateCachedImpl(VideoAwardAwardTypeAPI typeAPI, int ordinal) {
        this.awardId = typeAPI.getAwardIdBoxed(ordinal);
        this.sequenceNumber = typeAPI.getSequenceNumberBoxed(ordinal);
        this.winner = typeAPI.getWinnerBoxed(ordinal);
        this.year = typeAPI.getYearBoxed(ordinal);
        this.personId = typeAPI.getPersonIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getAwardId(int ordinal) {
        return awardId.longValue();
    }

    public Long getAwardIdBoxed(int ordinal) {
        return awardId;
    }

    public long getSequenceNumber(int ordinal) {
        return sequenceNumber.longValue();
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return sequenceNumber;
    }

    public boolean getWinner(int ordinal) {
        return winner.booleanValue();
    }

    public Boolean getWinnerBoxed(int ordinal) {
        return winner;
    }

    public long getYear(int ordinal) {
        return year.longValue();
    }

    public Long getYearBoxed(int ordinal) {
        return year;
    }

    public long getPersonId(int ordinal) {
        return personId.longValue();
    }

    public Long getPersonIdBoxed(int ordinal) {
        return personId;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoAwardAwardTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoAwardAwardTypeAPI) typeAPI;
    }

}