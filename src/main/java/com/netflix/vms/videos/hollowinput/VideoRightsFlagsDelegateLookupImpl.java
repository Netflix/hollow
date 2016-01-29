package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoRightsFlagsDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoRightsFlagsDelegate {

    private final VideoRightsFlagsTypeAPI typeAPI;

    public VideoRightsFlagsDelegateLookupImpl(VideoRightsFlagsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public boolean getSearchOnly(int ordinal) {
        return typeAPI.getSearchOnly(ordinal);
    }

    public Boolean getSearchOnlyBoxed(int ordinal) {
        return typeAPI.getSearchOnlyBoxed(ordinal);
    }

    public boolean getLocalText(int ordinal) {
        return typeAPI.getLocalText(ordinal);
    }

    public Boolean getLocalTextBoxed(int ordinal) {
        return typeAPI.getLocalTextBoxed(ordinal);
    }

    public boolean getLanguageOverride(int ordinal) {
        return typeAPI.getLanguageOverride(ordinal);
    }

    public Boolean getLanguageOverrideBoxed(int ordinal) {
        return typeAPI.getLanguageOverrideBoxed(ordinal);
    }

    public boolean getLocalAudio(int ordinal) {
        return typeAPI.getLocalAudio(ordinal);
    }

    public Boolean getLocalAudioBoxed(int ordinal) {
        return typeAPI.getLocalAudioBoxed(ordinal);
    }

    public int getFirstDisplayDatesOrdinal(int ordinal) {
        return typeAPI.getFirstDisplayDatesOrdinal(ordinal);
    }

    public boolean getGoLive(int ordinal) {
        return typeAPI.getGoLive(ordinal);
    }

    public Boolean getGoLiveBoxed(int ordinal) {
        return typeAPI.getGoLiveBoxed(ordinal);
    }

    public boolean getAutoPlay(int ordinal) {
        return typeAPI.getAutoPlay(ordinal);
    }

    public Boolean getAutoPlayBoxed(int ordinal) {
        return typeAPI.getAutoPlayBoxed(ordinal);
    }

    public long getFirstDisplayDate(int ordinal) {
        return typeAPI.getFirstDisplayDate(ordinal);
    }

    public Long getFirstDisplayDateBoxed(int ordinal) {
        return typeAPI.getFirstDisplayDateBoxed(ordinal);
    }

    public VideoRightsFlagsTypeAPI getTypeAPI() {
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