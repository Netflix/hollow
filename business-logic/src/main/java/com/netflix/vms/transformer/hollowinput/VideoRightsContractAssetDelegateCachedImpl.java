package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoRightsContractAssetDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoRightsContractAssetDelegate {

    private final int bcp47CodeOrdinal;
    private final int assetTypeOrdinal;
   private VideoRightsContractAssetTypeAPI typeAPI;

    public VideoRightsContractAssetDelegateCachedImpl(VideoRightsContractAssetTypeAPI typeAPI, int ordinal) {
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

    public VideoRightsContractAssetTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoRightsContractAssetTypeAPI) typeAPI;
    }

}