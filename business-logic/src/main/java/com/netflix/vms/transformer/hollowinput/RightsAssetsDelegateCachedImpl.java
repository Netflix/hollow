package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class RightsAssetsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RightsAssetsDelegate {

    private final int assetSetIdOrdinal;
    private final int assetsOrdinal;
    private RightsAssetsTypeAPI typeAPI;

    public RightsAssetsDelegateCachedImpl(RightsAssetsTypeAPI typeAPI, int ordinal) {
        this.assetSetIdOrdinal = typeAPI.getAssetSetIdOrdinal(ordinal);
        this.assetsOrdinal = typeAPI.getAssetsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getAssetSetIdOrdinal(int ordinal) {
        return assetSetIdOrdinal;
    }

    public int getAssetsOrdinal(int ordinal) {
        return assetsOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public RightsAssetsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RightsAssetsTypeAPI) typeAPI;
    }

}