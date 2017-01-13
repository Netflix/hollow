package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StreamAssetMetadataDelegateLookupImpl extends HollowObjectAbstractDelegate implements StreamAssetMetadataDelegate {

    private final StreamAssetMetadataTypeAPI typeAPI;

    public StreamAssetMetadataDelegateLookupImpl(StreamAssetMetadataTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getId(int ordinal) {
        return typeAPI.getId(ordinal);
    }

    public boolean isIdEqual(int ordinal, String testValue) {
        return typeAPI.isIdEqual(ordinal, testValue);
    }

    public StreamAssetMetadataTypeAPI getTypeAPI() {
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