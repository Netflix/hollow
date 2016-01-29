package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class AssetMetaDatasTypeAPI extends HollowObjectTypeAPI {

    private final AssetMetaDatasDelegateLookupImpl delegateLookupImpl;

    AssetMetaDatasTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
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

    public AssetMetaDatasTrackLabelsTypeAPI getTrackLabelsTypeAPI() {
        return getAPI().getAssetMetaDatasTrackLabelsTypeAPI();
    }

    public AssetMetaDatasDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}