package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class AssetMetaDatasDelegateLookupImpl extends HollowObjectAbstractDelegate implements AssetMetaDatasDelegate {

    private final AssetMetaDatasTypeAPI typeAPI;

    public AssetMetaDatasDelegateLookupImpl(AssetMetaDatasTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getAssetIdOrdinal(int ordinal) {
        return typeAPI.getAssetIdOrdinal(ordinal);
    }

    public int getTrackLabelsOrdinal(int ordinal) {
        return typeAPI.getTrackLabelsOrdinal(ordinal);
    }

    public AssetMetaDatasTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}