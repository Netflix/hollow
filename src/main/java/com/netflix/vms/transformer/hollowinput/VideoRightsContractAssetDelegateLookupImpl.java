package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoRightsContractAssetDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoRightsContractAssetDelegate {

    private final VideoRightsContractAssetTypeAPI typeAPI;

    public VideoRightsContractAssetDelegateLookupImpl(VideoRightsContractAssetTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getBcp47CodeOrdinal(int ordinal) {
        return typeAPI.getBcp47CodeOrdinal(ordinal);
    }

    public int getAssetTypeOrdinal(int ordinal) {
        return typeAPI.getAssetTypeOrdinal(ordinal);
    }

    public VideoRightsContractAssetTypeAPI getTypeAPI() {
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