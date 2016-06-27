package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class RightsContractAssetDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RightsContractAssetDelegate {

    private final int bcp47CodeOrdinal;
    private final int assetTypeOrdinal;
   private RightsContractAssetTypeAPI typeAPI;

    public RightsContractAssetDelegateCachedImpl(RightsContractAssetTypeAPI typeAPI, int ordinal) {
        this.bcp47CodeOrdinal = typeAPI.getBcp47CodeOrdinal(ordinal);
        this.assetTypeOrdinal = typeAPI.getAssetTypeOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getBcp47CodeOrdinal(int ordinal) {
        return bcp47CodeOrdinal;
    }

    public int getAssetTypeOrdinal(int ordinal) {
        return assetTypeOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public RightsContractAssetTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RightsContractAssetTypeAPI) typeAPI;
    }

}