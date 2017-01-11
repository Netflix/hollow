package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class AssetMetaDatasTypeAPI extends HollowObjectTypeAPI {

    private final AssetMetaDatasDelegateLookupImpl delegateLookupImpl;

    AssetMetaDatasTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "assetId",
            "trackLabels"
        });
        this.delegateLookupImpl = new AssetMetaDatasDelegateLookupImpl(this);
    }

    public int getAssetIdOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("AssetMetaDatas", ordinal, "assetId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getAssetIdTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getTrackLabelsOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("AssetMetaDatas", ordinal, "trackLabels");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public TranslatedTextTypeAPI getTrackLabelsTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public AssetMetaDatasDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}