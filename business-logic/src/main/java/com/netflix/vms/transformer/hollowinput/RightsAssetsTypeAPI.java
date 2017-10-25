package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class RightsAssetsTypeAPI extends HollowObjectTypeAPI {

    private final RightsAssetsDelegateLookupImpl delegateLookupImpl;

    public RightsAssetsTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "assetSetId",
            "assets"
        });
        this.delegateLookupImpl = new RightsAssetsDelegateLookupImpl(this);
    }

    public int getAssetSetIdOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("RightsAssets", ordinal, "assetSetId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public RightsAssetSetIdTypeAPI getAssetSetIdTypeAPI() {
        return getAPI().getRightsAssetSetIdTypeAPI();
    }

    public int getAssetsOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("RightsAssets", ordinal, "assets");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public SetOfRightsAssetTypeAPI getAssetsTypeAPI() {
        return getAPI().getSetOfRightsAssetTypeAPI();
    }

    public RightsAssetsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}