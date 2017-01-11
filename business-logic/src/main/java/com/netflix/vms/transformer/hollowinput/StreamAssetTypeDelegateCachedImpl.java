package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class StreamAssetTypeDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, StreamAssetTypeDelegate {

    private final Long assetTypeId;
    private final int assetTypeOrdinal;
   private StreamAssetTypeTypeAPI typeAPI;

    public StreamAssetTypeDelegateCachedImpl(StreamAssetTypeTypeAPI typeAPI, int ordinal) {
        this.assetTypeId = typeAPI.getAssetTypeIdBoxed(ordinal);
        this.assetTypeOrdinal = typeAPI.getAssetTypeOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getAssetTypeId(int ordinal) {
        return assetTypeId.longValue();
    }

    public Long getAssetTypeIdBoxed(int ordinal) {
        return assetTypeId;
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

    public StreamAssetTypeTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (StreamAssetTypeTypeAPI) typeAPI;
    }

}