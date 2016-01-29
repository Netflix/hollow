package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class AssetMetaDatasTrackLabelsTypeAPI extends HollowObjectTypeAPI {

    private final AssetMetaDatasTrackLabelsDelegateLookupImpl delegateLookupImpl;

    AssetMetaDatasTrackLabelsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "translatedTexts"
        });
        this.delegateLookupImpl = new AssetMetaDatasTrackLabelsDelegateLookupImpl(this);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("AssetMetaDatasTrackLabels", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public AssetMetaDatasTrackLabelsMapOfTranslatedTextsTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getAssetMetaDatasTrackLabelsMapOfTranslatedTextsTypeAPI();
    }

    public AssetMetaDatasTrackLabelsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}