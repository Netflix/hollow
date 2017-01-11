package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class DisallowedAssetBundleTypeAPI extends HollowObjectTypeAPI {

    private final DisallowedAssetBundleDelegateLookupImpl delegateLookupImpl;

    DisallowedAssetBundleTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "forceSubtitle",
            "audioLanguageCode",
            "disallowedSubtitleLangCodes"
        });
        this.delegateLookupImpl = new DisallowedAssetBundleDelegateLookupImpl(this);
    }

    public boolean getForceSubtitle(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("DisallowedAssetBundle", ordinal, "forceSubtitle") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]) == Boolean.TRUE;
    }

    public Boolean getForceSubtitleBoxed(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("DisallowedAssetBundle", ordinal, "forceSubtitle");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]);
    }



    public int getAudioLanguageCodeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("DisallowedAssetBundle", ordinal, "audioLanguageCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getAudioLanguageCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getDisallowedSubtitleLangCodesOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("DisallowedAssetBundle", ordinal, "disallowedSubtitleLangCodes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public DisallowedSubtitleLangCodesListTypeAPI getDisallowedSubtitleLangCodesTypeAPI() {
        return getAPI().getDisallowedSubtitleLangCodesListTypeAPI();
    }

    public DisallowedAssetBundleDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}