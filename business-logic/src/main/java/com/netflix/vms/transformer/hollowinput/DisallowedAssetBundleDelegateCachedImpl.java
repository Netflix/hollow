package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class DisallowedAssetBundleDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, DisallowedAssetBundleDelegate {

    private final Boolean forceSubtitle;
    private final int audioLanguageCodeOrdinal;
    private final int disallowedSubtitleLangCodesOrdinal;
   private DisallowedAssetBundleTypeAPI typeAPI;

    public DisallowedAssetBundleDelegateCachedImpl(DisallowedAssetBundleTypeAPI typeAPI, int ordinal) {
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

    public DisallowedAssetBundleTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (DisallowedAssetBundleTypeAPI) typeAPI;
    }

}