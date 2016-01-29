package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoRightsRightsContractsAssetsTypeAPI extends HollowObjectTypeAPI {

    private final VideoRightsRightsContractsAssetsDelegateLookupImpl delegateLookupImpl;

    VideoRightsRightsContractsAssetsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "bcp47Code",
            "assetType"
        });
        this.delegateLookupImpl = new VideoRightsRightsContractsAssetsDelegateLookupImpl(this);
    }

    public int getBcp47CodeOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsRightsContractsAssets", ordinal, "bcp47Code");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getBcp47CodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getAssetTypeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsRightsContractsAssets", ordinal, "assetType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getAssetTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public VideoRightsRightsContractsAssetsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}