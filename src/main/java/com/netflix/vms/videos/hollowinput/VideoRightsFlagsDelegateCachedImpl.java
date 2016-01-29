package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoRightsFlagsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoRightsFlagsDelegate {

    private final Boolean searchOnly;
    private final Boolean localText;
    private final Boolean languageOverride;
    private final Boolean localAudio;
    private final int firstDisplayDatesOrdinal;
    private final Boolean goLive;
    private final Boolean autoPlay;
    private final Long firstDisplayDate;
   private VideoRightsFlagsTypeAPI typeAPI;

    public VideoRightsFlagsDelegateCachedImpl(VideoRightsFlagsTypeAPI typeAPI, int ordinal) {
        this.searchOnly = typeAPI.getSearchOnlyBoxed(ordinal);
        this.localText = typeAPI.getLocalTextBoxed(ordinal);
        this.languageOverride = typeAPI.getLanguageOverrideBoxed(ordinal);
        this.localAudio = typeAPI.getLocalAudioBoxed(ordinal);
        this.firstDisplayDatesOrdinal = typeAPI.getFirstDisplayDatesOrdinal(ordinal);
        this.goLive = typeAPI.getGoLiveBoxed(ordinal);
        this.autoPlay = typeAPI.getAutoPlayBoxed(ordinal);
        this.firstDisplayDate = typeAPI.getFirstDisplayDateBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public boolean getSearchOnly(int ordinal) {
        return searchOnly.booleanValue();
    }

    public Boolean getSearchOnlyBoxed(int ordinal) {
        return searchOnly;
    }

    public boolean getLocalText(int ordinal) {
        return localText.booleanValue();
    }

    public Boolean getLocalTextBoxed(int ordinal) {
        return localText;
    }

    public boolean getLanguageOverride(int ordinal) {
        return languageOverride.booleanValue();
    }

    public Boolean getLanguageOverrideBoxed(int ordinal) {
        return languageOverride;
    }

    public boolean getLocalAudio(int ordinal) {
        return localAudio.booleanValue();
    }

    public Boolean getLocalAudioBoxed(int ordinal) {
        return localAudio;
    }

    public int getFirstDisplayDatesOrdinal(int ordinal) {
        return firstDisplayDatesOrdinal;
    }

    public boolean getGoLive(int ordinal) {
        return goLive.booleanValue();
    }

    public Boolean getGoLiveBoxed(int ordinal) {
        return goLive;
    }

    public boolean getAutoPlay(int ordinal) {
        return autoPlay.booleanValue();
    }

    public Boolean getAutoPlayBoxed(int ordinal) {
        return autoPlay;
    }

    public long getFirstDisplayDate(int ordinal) {
        return firstDisplayDate.longValue();
    }

    public Long getFirstDisplayDateBoxed(int ordinal) {
        return firstDisplayDate;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoRightsFlagsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoRightsFlagsTypeAPI) typeAPI;
    }

}