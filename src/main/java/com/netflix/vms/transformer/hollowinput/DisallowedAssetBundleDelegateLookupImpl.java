package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class DisallowedAssetBundleDelegateLookupImpl extends HollowObjectAbstractDelegate implements DisallowedAssetBundleDelegate {

    private final DisallowedAssetBundleTypeAPI typeAPI;

    public DisallowedAssetBundleDelegateLookupImpl(DisallowedAssetBundleTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public boolean getForceSubtitle(int ordinal) {
        return typeAPI.getForceSubtitle(ordinal);
    }

    public Boolean getForceSubtitleBoxed(int ordinal) {
        return typeAPI.getForceSubtitleBoxed(ordinal);
    }

    public int getAudioLanguageCodeOrdinal(int ordinal) {
        return typeAPI.getAudioLanguageCodeOrdinal(ordinal);
    }

    public int getDisallowedSubtitleLangCodesOrdinal(int ordinal) {
        return typeAPI.getDisallowedSubtitleLangCodesOrdinal(ordinal);
    }

    public DisallowedAssetBundleTypeAPI getTypeAPI() {
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