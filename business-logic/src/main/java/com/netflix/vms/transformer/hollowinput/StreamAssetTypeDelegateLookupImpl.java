package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StreamAssetTypeDelegateLookupImpl extends HollowObjectAbstractDelegate implements StreamAssetTypeDelegate {

    private final StreamAssetTypeTypeAPI typeAPI;

    public StreamAssetTypeDelegateLookupImpl(StreamAssetTypeTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getAssetTypeId(int ordinal) {
        return typeAPI.getAssetTypeId(ordinal);
    }

    public Long getAssetTypeIdBoxed(int ordinal) {
        return typeAPI.getAssetTypeIdBoxed(ordinal);
    }

    public int getAssetTypeOrdinal(int ordinal) {
        return typeAPI.getAssetTypeOrdinal(ordinal);
    }

    public StreamAssetTypeTypeAPI getTypeAPI() {
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