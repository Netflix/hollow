package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class StreamAssetMetadataDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, StreamAssetMetadataDelegate {

    private final String id;
   private StreamAssetMetadataTypeAPI typeAPI;

    public StreamAssetMetadataDelegateCachedImpl(StreamAssetMetadataTypeAPI typeAPI, int ordinal) {
        this.id = typeAPI.getId(ordinal);
        this.typeAPI = typeAPI;
    }

    public String getId(int ordinal) {
        return id;
    }

    public boolean isIdEqual(int ordinal, String testValue) {
        if(testValue == null)
            return id == null;
        return testValue.equals(id);
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public StreamAssetMetadataTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (StreamAssetMetadataTypeAPI) typeAPI;
    }

}