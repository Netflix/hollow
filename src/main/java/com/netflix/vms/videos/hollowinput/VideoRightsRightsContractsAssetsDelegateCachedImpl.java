package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoRightsRightsContractsAssetsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoRightsRightsContractsAssetsDelegate {

    private final int bcp47CodeOrdinal;
    private final int assetTypeOrdinal;
   private VideoRightsRightsContractsAssetsTypeAPI typeAPI;

    public VideoRightsRightsContractsAssetsDelegateCachedImpl(VideoRightsRightsContractsAssetsTypeAPI typeAPI, int ordinal) {
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

    public VideoRightsRightsContractsAssetsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoRightsRightsContractsAssetsTypeAPI) typeAPI;
    }

}