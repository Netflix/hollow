package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoRightsRightsContractsDisallowedAssetBundlesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoRightsRightsContractsDisallowedAssetBundlesDelegate {

    private final Boolean forceSubtitle;
    private final int audioLanguageCodeOrdinal;
    private final int disallowedSubtitleLangCodesOrdinal;
   private VideoRightsRightsContractsDisallowedAssetBundlesTypeAPI typeAPI;

    public VideoRightsRightsContractsDisallowedAssetBundlesDelegateCachedImpl(VideoRightsRightsContractsDisallowedAssetBundlesTypeAPI typeAPI, int ordinal) {
        this.forceSubtitle = typeAPI.getForceSubtitleBoxed(ordinal);
        this.audioLanguageCodeOrdinal = typeAPI.getAudioLanguageCodeOrdinal(ordinal);
        this.disallowedSubtitleLangCodesOrdinal = typeAPI.getDisallowedSubtitleLangCodesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public boolean getForceSubtitle(int ordinal) {
        return forceSubtitle.booleanValue();
    }

    public Boolean getForceSubtitleBoxed(int ordinal) {
        return forceSubtitle;
    }

    public int getAudioLanguageCodeOrdinal(int ordinal) {
        return audioLanguageCodeOrdinal;
    }

    public int getDisallowedSubtitleLangCodesOrdinal(int ordinal) {
        return disallowedSubtitleLangCodesOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoRightsRightsContractsDisallowedAssetBundlesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoRightsRightsContractsDisallowedAssetBundlesTypeAPI) typeAPI;
    }

}