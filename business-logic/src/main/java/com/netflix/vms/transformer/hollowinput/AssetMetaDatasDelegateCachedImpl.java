package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class AssetMetaDatasDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, AssetMetaDatasDelegate {

    private final int assetIdOrdinal;
    private final int trackLabelsOrdinal;
   private AssetMetaDatasTypeAPI typeAPI;

    public AssetMetaDatasDelegateCachedImpl(AssetMetaDatasTypeAPI typeAPI, int ordinal) {
        this.assetIdOrdinal = typeAPI.getAssetIdOrdinal(ordinal);
        this.trackLabelsOrdinal = typeAPI.getTrackLabelsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getAssetIdOrdinal(int ordinal) {
        return assetIdOrdinal;
    }

    public int getTrackLabelsOrdinal(int ordinal) {
        return trackLabelsOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public AssetMetaDatasTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (AssetMetaDatasTypeAPI) typeAPI;
    }

}