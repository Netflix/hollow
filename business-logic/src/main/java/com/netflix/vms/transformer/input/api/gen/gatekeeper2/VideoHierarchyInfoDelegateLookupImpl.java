package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoHierarchyInfoDelegateLookupImpl extends HollowObjectAbstractDelegate implements
        VideoHierarchyInfoDelegate {

    private final VideoHierarchyInfoTypeAPI typeAPI;

    public VideoHierarchyInfoDelegateLookupImpl(VideoHierarchyInfoTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getTopNodeId(int ordinal) {
        ordinal = typeAPI.getTopNodeIdOrdinal(ordinal);
        return ordinal == -1 ? Long.MIN_VALUE : typeAPI.getAPI().getParentNodeIdTypeAPI().getValue(ordinal);
    }

    public Long getTopNodeIdBoxed(int ordinal) {
        ordinal = typeAPI.getTopNodeIdOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getParentNodeIdTypeAPI().getValueBoxed(ordinal);
    }

    public int getTopNodeIdOrdinal(int ordinal) {
        return typeAPI.getTopNodeIdOrdinal(ordinal);
    }

    public long getParentId(int ordinal) {
        ordinal = typeAPI.getParentIdOrdinal(ordinal);
        return ordinal == -1 ? Long.MIN_VALUE : typeAPI.getAPI().getParentNodeIdTypeAPI().getValue(ordinal);
    }

    public Long getParentIdBoxed(int ordinal) {
        ordinal = typeAPI.getParentIdOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getParentNodeIdTypeAPI().getValueBoxed(ordinal);
    }

    public int getParentIdOrdinal(int ordinal) {
        return typeAPI.getParentIdOrdinal(ordinal);
    }

    public String getNodeType(int ordinal) {
        ordinal = typeAPI.getNodeTypeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getVideoNodeTypeTypeAPI().getNodeType(ordinal);
    }

    public boolean isNodeTypeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getNodeTypeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getVideoNodeTypeTypeAPI().isNodeTypeEqual(ordinal, testValue);
    }

    public int getNodeTypeOrdinal(int ordinal) {
        return typeAPI.getNodeTypeOrdinal(ordinal);
    }

    public VideoHierarchyInfoTypeAPI getTypeAPI() {
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