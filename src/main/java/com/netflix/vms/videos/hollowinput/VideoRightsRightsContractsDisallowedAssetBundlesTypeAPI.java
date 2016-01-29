package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoRightsRightsContractsDisallowedAssetBundlesTypeAPI extends HollowObjectTypeAPI {

    private final VideoRightsRightsContractsDisallowedAssetBundlesDelegateLookupImpl delegateLookupImpl;

    VideoRightsRightsContractsDisallowedAssetBundlesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "forceSubtitle",
            "audioLanguageCode",
            "disallowedSubtitleLangCodes"
        });
        this.delegateLookupImpl = new VideoRightsRightsContractsDisallowedAssetBundlesDelegateLookupImpl(this);
    }

    public boolean getForceSubtitle(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("VideoRightsRightsContractsDisallowedAssetBundles", ordinal, "forceSubtitle") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]) == Boolean.TRUE;
    }

    public Boolean getForceSubtitleBoxed(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("VideoRightsRightsContractsDisallowedAssetBundles", ordinal, "forceSubtitle");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]);
    }



    public int getAudioLanguageCodeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsRightsContractsDisallowedAssetBundles", ordinal, "audioLanguageCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getAudioLanguageCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getDisallowedSubtitleLangCodesOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsRightsContractsDisallowedAssetBundles", ordinal, "disallowedSubtitleLangCodes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesTypeAPI getDisallowedSubtitleLangCodesTypeAPI() {
        return getAPI().getVideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesTypeAPI();
    }

    public VideoRightsRightsContractsDisallowedAssetBundlesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}