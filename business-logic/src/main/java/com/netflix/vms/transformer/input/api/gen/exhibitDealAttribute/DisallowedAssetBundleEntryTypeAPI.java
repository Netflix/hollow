package com.netflix.vms.transformer.input.api.gen.exhibitDealAttribute;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class DisallowedAssetBundleEntryTypeAPI extends HollowObjectTypeAPI {

    private final DisallowedAssetBundleEntryDelegateLookupImpl delegateLookupImpl;

    public DisallowedAssetBundleEntryTypeAPI(ExhibitDealAttributeV1API api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "audioLanguageCode",
            "forceSubtitle",
            "disallowedSubtitleLangCodes"
        });
        this.delegateLookupImpl = new DisallowedAssetBundleEntryDelegateLookupImpl(this);
    }

    public int getAudioLanguageCodeOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("DisallowedAssetBundleEntry", ordinal, "audioLanguageCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getAudioLanguageCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getForceSubtitleOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("DisallowedAssetBundleEntry", ordinal, "forceSubtitle");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public BooleanTypeAPI getForceSubtitleTypeAPI() {
        return getAPI().getBooleanTypeAPI();
    }

    public int getDisallowedSubtitleLangCodesOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("DisallowedAssetBundleEntry", ordinal, "disallowedSubtitleLangCodes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public SetOfStringTypeAPI getDisallowedSubtitleLangCodesTypeAPI() {
        return getAPI().getSetOfStringTypeAPI();
    }

    public DisallowedAssetBundleEntryDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public ExhibitDealAttributeV1API getAPI() {
        return (ExhibitDealAttributeV1API) api;
    }

}