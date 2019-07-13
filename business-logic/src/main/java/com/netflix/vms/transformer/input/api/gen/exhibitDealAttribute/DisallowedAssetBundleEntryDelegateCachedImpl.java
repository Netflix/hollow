package com.netflix.vms.transformer.input.api.gen.exhibitDealAttribute;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DisallowedAssetBundleEntryDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, DisallowedAssetBundleEntryDelegate {

    private final String audioLanguageCode;
    private final int audioLanguageCodeOrdinal;
    private final Boolean forceSubtitle;
    private final int forceSubtitleOrdinal;
    private final int disallowedSubtitleLangCodesOrdinal;
    private DisallowedAssetBundleEntryTypeAPI typeAPI;

    public DisallowedAssetBundleEntryDelegateCachedImpl(DisallowedAssetBundleEntryTypeAPI typeAPI, int ordinal) {
        this.audioLanguageCodeOrdinal = typeAPI.getAudioLanguageCodeOrdinal(ordinal);
        int audioLanguageCodeTempOrdinal = audioLanguageCodeOrdinal;
        this.audioLanguageCode = audioLanguageCodeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(audioLanguageCodeTempOrdinal);
        this.forceSubtitleOrdinal = typeAPI.getForceSubtitleOrdinal(ordinal);
        int forceSubtitleTempOrdinal = forceSubtitleOrdinal;
        this.forceSubtitle = forceSubtitleTempOrdinal == -1 ? null : typeAPI.getAPI().getBooleanTypeAPI().getValue(forceSubtitleTempOrdinal);
        this.disallowedSubtitleLangCodesOrdinal = typeAPI.getDisallowedSubtitleLangCodesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public String getAudioLanguageCode(int ordinal) {
        return audioLanguageCode;
    }

    public boolean isAudioLanguageCodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return audioLanguageCode == null;
        return testValue.equals(audioLanguageCode);
    }

    public int getAudioLanguageCodeOrdinal(int ordinal) {
        return audioLanguageCodeOrdinal;
    }

    public boolean getForceSubtitle(int ordinal) {
        if(forceSubtitle == null)
            return false;
        return forceSubtitle.booleanValue();
    }

    public Boolean getForceSubtitleBoxed(int ordinal) {
        return forceSubtitle;
    }

    public int getForceSubtitleOrdinal(int ordinal) {
        return forceSubtitleOrdinal;
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

    public DisallowedAssetBundleEntryTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (DisallowedAssetBundleEntryTypeAPI) typeAPI;
    }

}