package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoNodeTypeDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoNodeTypeDelegate {

    private final VideoNodeTypeTypeAPI typeAPI;

    public VideoNodeTypeDelegateLookupImpl(VideoNodeTypeTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getNodeType(int ordinal) {
        return typeAPI.getNodeType(ordinal);
    }

    public boolean isNodeTypeEqual(int ordinal, String testValue) {
        return typeAPI.isNodeTypeEqual(ordinal, testValue);
    }

    public VideoNodeTypeTypeAPI getTypeAPI() {
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