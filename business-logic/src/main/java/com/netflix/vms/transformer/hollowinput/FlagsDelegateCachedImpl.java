package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class FlagsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, FlagsDelegate {

    private final Boolean searchOnly;
    private final Boolean localText;
    private final Boolean languageOverride;
    private final Boolean localAudio;
    private final Boolean goLive;
    private final Boolean contentApproved;
    private final Boolean autoPlay;
    private final int firstDisplayDateOrdinal;
    private final int firstDisplayDatesOrdinal;
   private FlagsTypeAPI typeAPI;

    public FlagsDelegateCachedImpl(FlagsTypeAPI typeAPI, int ordinal) {
        this.searchOnly = typeAPI.getSearchOnlyBoxed(ordinal);
        this.localText = typeAPI.getLocalTextBoxed(ordinal);
        this.languageOverride = typeAPI.getLanguageOverrideBoxed(ordinal);
        this.localAudio = typeAPI.getLocalAudioBoxed(ordinal);
        this.goLive = typeAPI.getGoLiveBoxed(ordinal);
        this.contentApproved = typeAPI.getContentApprovedBoxed(ordinal);
        this.autoPlay = typeAPI.getAutoPlayBoxed(ordinal);
        this.firstDisplayDateOrdinal = typeAPI.getFirstDisplayDateOrdinal(ordinal);
        this.firstDisplayDatesOrdinal = typeAPI.getFirstDisplayDatesOrdinal(ordinal);
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

    public boolean getGoLive(int ordinal) {
        return goLive.booleanValue();
    }

    public Boolean getGoLiveBoxed(int ordinal) {
        return goLive;
    }

    public boolean getContentApproved(int ordinal) {
        return contentApproved.booleanValue();
    }

    public Boolean getContentApprovedBoxed(int ordinal) {
        return contentApproved;
    }

    public boolean getAutoPlay(int ordinal) {
        return autoPlay.booleanValue();
    }

    public Boolean getAutoPlayBoxed(int ordinal) {
        return autoPlay;
    }

    public int getFirstDisplayDateOrdinal(int ordinal) {
        return firstDisplayDateOrdinal;
    }

    public int getFirstDisplayDatesOrdinal(int ordinal) {
        return firstDisplayDatesOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public FlagsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (FlagsTypeAPI) typeAPI;
    }

}