package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoNodeTypeDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate,
        VideoNodeTypeDelegate {

    private final String nodeType;
    private VideoNodeTypeTypeAPI typeAPI;

    public VideoNodeTypeDelegateCachedImpl(VideoNodeTypeTypeAPI typeAPI, int ordinal) {
        this.nodeType = typeAPI.getNodeType(ordinal);
        this.typeAPI = typeAPI;
    }

    public String getNodeType(int ordinal) {
        return nodeType;
    }

    public boolean isNodeTypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return nodeType == null;
        return testValue.equals(nodeType);
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoNodeTypeTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoNodeTypeTypeAPI) typeAPI;
    }

}