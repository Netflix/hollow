package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoHierarchyInfoDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoHierarchyInfoDelegate {

    private final Long topNodeId;
    private final int topNodeIdOrdinal;
    private final Long parentId;
    private final int parentIdOrdinal;
    private final String nodeType;
    private final int nodeTypeOrdinal;
    private VideoHierarchyInfoTypeAPI typeAPI;

    public VideoHierarchyInfoDelegateCachedImpl(VideoHierarchyInfoTypeAPI typeAPI, int ordinal) {
        this.topNodeIdOrdinal = typeAPI.getTopNodeIdOrdinal(ordinal);
        int topNodeIdTempOrdinal = topNodeIdOrdinal;
        this.topNodeId = topNodeIdTempOrdinal == -1 ? null : typeAPI.getAPI().getParentNodeIdTypeAPI().getValue(topNodeIdTempOrdinal);
        this.parentIdOrdinal = typeAPI.getParentIdOrdinal(ordinal);
        int parentIdTempOrdinal = parentIdOrdinal;
        this.parentId = parentIdTempOrdinal == -1 ? null : typeAPI.getAPI().getParentNodeIdTypeAPI().getValue(parentIdTempOrdinal);
        this.nodeTypeOrdinal = typeAPI.getNodeTypeOrdinal(ordinal);
        int nodeTypeTempOrdinal = nodeTypeOrdinal;
        this.nodeType = nodeTypeTempOrdinal == -1 ? null : typeAPI.getAPI().getVideoNodeTypeTypeAPI().getNodeType(nodeTypeTempOrdinal);
        this.typeAPI = typeAPI;
    }

    public long getTopNodeId(int ordinal) {
        if(topNodeId == null)
            return Long.MIN_VALUE;
        return topNodeId.longValue();
    }

    public Long getTopNodeIdBoxed(int ordinal) {
        return topNodeId;
    }

    public int getTopNodeIdOrdinal(int ordinal) {
        return topNodeIdOrdinal;
    }

    public long getParentId(int ordinal) {
        if(parentId == null)
            return Long.MIN_VALUE;
        return parentId.longValue();
    }

    public Long getParentIdBoxed(int ordinal) {
        return parentId;
    }

    public int getParentIdOrdinal(int ordinal) {
        return parentIdOrdinal;
    }

    public String getNodeType(int ordinal) {
        return nodeType;
    }

    public boolean isNodeTypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return nodeType == null;
        return testValue.equals(nodeType);
    }

    public int getNodeTypeOrdinal(int ordinal) {
        return nodeTypeOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoHierarchyInfoTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoHierarchyInfoTypeAPI) typeAPI;
    }

}