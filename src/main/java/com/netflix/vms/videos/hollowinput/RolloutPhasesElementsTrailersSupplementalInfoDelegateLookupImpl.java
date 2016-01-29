package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhasesElementsTrailersSupplementalInfoDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutPhasesElementsTrailersSupplementalInfoDelegate {

    private final RolloutPhasesElementsTrailersSupplementalInfoTypeAPI typeAPI;

    public RolloutPhasesElementsTrailersSupplementalInfoDelegateLookupImpl(RolloutPhasesElementsTrailersSupplementalInfoTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getImageBackgroundToneOrdinal(int ordinal) {
        return typeAPI.getImageBackgroundToneOrdinal(ordinal);
    }

    public long getVideoLength(int ordinal) {
        return typeAPI.getVideoLength(ordinal);
    }

    public Long getVideoLengthBoxed(int ordinal) {
        return typeAPI.getVideoLengthBoxed(ordinal);
    }

    public int getSubtitleLocaleOrdinal(int ordinal) {
        return typeAPI.getSubtitleLocaleOrdinal(ordinal);
    }

    public long getSeasonNumber(int ordinal) {
        return typeAPI.getSeasonNumber(ordinal);
    }

    public Long getSeasonNumberBoxed(int ordinal) {
        return typeAPI.getSeasonNumberBoxed(ordinal);
    }

    public int getVideoOrdinal(int ordinal) {
        return typeAPI.getVideoOrdinal(ordinal);
    }

    public int getImageTagOrdinal(int ordinal) {
        return typeAPI.getImageTagOrdinal(ordinal);
    }

    public int getVideoValueOrdinal(int ordinal) {
        return typeAPI.getVideoValueOrdinal(ordinal);
    }

    public long getPriority(int ordinal) {
        return typeAPI.getPriority(ordinal);
    }

    public Long getPriorityBoxed(int ordinal) {
        return typeAPI.getPriorityBoxed(ordinal);
    }

    public RolloutPhasesElementsTrailersSupplementalInfoTypeAPI getTypeAPI() {
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