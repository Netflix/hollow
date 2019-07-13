package com.netflix.vms.transformer.input.api.gen.exhibitDealAttribute;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DisallowedAssetBundleEntryDelegateLookupImpl extends HollowObjectAbstractDelegate implements DisallowedAssetBundleEntryDelegate {

    private final DisallowedAssetBundleEntryTypeAPI typeAPI;

    public DisallowedAssetBundleEntryDelegateLookupImpl(DisallowedAssetBundleEntryTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getAudioLanguageCode(int ordinal) {
        ordinal = typeAPI.getAudioLanguageCodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isAudioLanguageCodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getAudioLanguageCodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getAudioLanguageCodeOrdinal(int ordinal) {
        return typeAPI.getAudioLanguageCodeOrdinal(ordinal);
    }

    public boolean getForceSubtitle(int ordinal) {
        ordinal = typeAPI.getForceSubtitleOrdinal(ordinal);
        return ordinal == -1 ? false : typeAPI.getAPI().getBooleanTypeAPI().getValue(ordinal);
    }

    public Boolean getForceSubtitleBoxed(int ordinal) {
        ordinal = typeAPI.getForceSubtitleOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getBooleanTypeAPI().getValueBoxed(ordinal);
    }

    public int getForceSubtitleOrdinal(int ordinal) {
        return typeAPI.getForceSubtitleOrdinal(ordinal);
    }

    public int getDisallowedSubtitleLangCodesOrdinal(int ordinal) {
        return typeAPI.getDisallowedSubtitleLangCodesOrdinal(ordinal);
    }

    public DisallowedAssetBundleEntryTypeAPI getTypeAPI() {
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