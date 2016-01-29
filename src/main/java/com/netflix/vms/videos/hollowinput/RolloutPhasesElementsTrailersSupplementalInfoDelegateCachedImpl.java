package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class RolloutPhasesElementsTrailersSupplementalInfoDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutPhasesElementsTrailersSupplementalInfoDelegate {

    private final int imageBackgroundToneOrdinal;
    private final Long videoLength;
    private final int subtitleLocaleOrdinal;
    private final Long seasonNumber;
    private final int videoOrdinal;
    private final int imageTagOrdinal;
    private final int videoValueOrdinal;
    private final Long priority;
   private RolloutPhasesElementsTrailersSupplementalInfoTypeAPI typeAPI;

    public RolloutPhasesElementsTrailersSupplementalInfoDelegateCachedImpl(RolloutPhasesElementsTrailersSupplementalInfoTypeAPI typeAPI, int ordinal) {
        this.imageBackgroundToneOrdinal = typeAPI.getImageBackgroundToneOrdinal(ordinal);
        this.videoLength = typeAPI.getVideoLengthBoxed(ordinal);
        this.subtitleLocaleOrdinal = typeAPI.getSubtitleLocaleOrdinal(ordinal);
        this.seasonNumber = typeAPI.getSeasonNumberBoxed(ordinal);
        this.videoOrdinal = typeAPI.getVideoOrdinal(ordinal);
        this.imageTagOrdinal = typeAPI.getImageTagOrdinal(ordinal);
        this.videoValueOrdinal = typeAPI.getVideoValueOrdinal(ordinal);
        this.priority = typeAPI.getPriorityBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getImageBackgroundToneOrdinal(int ordinal) {
        return imageBackgroundToneOrdinal;
    }

    public long getVideoLength(int ordinal) {
        return videoLength.longValue();
    }

    public Long getVideoLengthBoxed(int ordinal) {
        return videoLength;
    }

    public int getSubtitleLocaleOrdinal(int ordinal) {
        return subtitleLocaleOrdinal;
    }

    public long getSeasonNumber(int ordinal) {
        return seasonNumber.longValue();
    }

    public Long getSeasonNumberBoxed(int ordinal) {
        return seasonNumber;
    }

    public int getVideoOrdinal(int ordinal) {
        return videoOrdinal;
    }

    public int getImageTagOrdinal(int ordinal) {
        return imageTagOrdinal;
    }

    public int getVideoValueOrdinal(int ordinal) {
        return videoValueOrdinal;
    }

    public long getPriority(int ordinal) {
        return priority.longValue();
    }

    public Long getPriorityBoxed(int ordinal) {
        return priority;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public RolloutPhasesElementsTrailersSupplementalInfoTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RolloutPhasesElementsTrailersSupplementalInfoTypeAPI) typeAPI;
    }

}