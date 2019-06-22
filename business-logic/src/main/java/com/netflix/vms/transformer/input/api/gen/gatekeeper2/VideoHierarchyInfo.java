package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class VideoHierarchyInfo extends HollowObject {

    public VideoHierarchyInfo(VideoHierarchyInfoDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public Long getTopNodeIdBoxed() {
        return delegate().getTopNodeIdBoxed(ordinal);
    }

    public long getTopNodeId() {
        return delegate().getTopNodeId(ordinal);
    }

    public ParentNodeId getTopNodeIdHollowReference() {
        int refOrdinal = delegate().getTopNodeIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getParentNodeId(refOrdinal);
    }

    public Long getParentIdBoxed() {
        return delegate().getParentIdBoxed(ordinal);
    }

    public long getParentId() {
        return delegate().getParentId(ordinal);
    }

    public ParentNodeId getParentIdHollowReference() {
        int refOrdinal = delegate().getParentIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getParentNodeId(refOrdinal);
    }

    public String getNodeType() {
        return delegate().getNodeType(ordinal);
    }

    public boolean isNodeTypeEqual(String testValue) {
        return delegate().isNodeTypeEqual(ordinal, testValue);
    }

    public VideoNodeType getNodeTypeHollowReference() {
        int refOrdinal = delegate().getNodeTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoNodeType(refOrdinal);
    }

    public Gk2StatusAPI api() {
        return typeApi().getAPI();
    }

    public VideoHierarchyInfoTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoHierarchyInfoDelegate delegate() {
        return (VideoHierarchyInfoDelegate)delegate;
    }

}