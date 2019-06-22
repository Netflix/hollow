package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VideoHierarchyInfoDelegate extends HollowObjectDelegate {

    public long getTopNodeId(int ordinal);

    public Long getTopNodeIdBoxed(int ordinal);

    public int getTopNodeIdOrdinal(int ordinal);

    public long getParentId(int ordinal);

    public Long getParentIdBoxed(int ordinal);

    public int getParentIdOrdinal(int ordinal);

    public String getNodeType(int ordinal);

    public boolean isNodeTypeEqual(int ordinal, String testValue);

    public int getNodeTypeOrdinal(int ordinal);

    public VideoHierarchyInfoTypeAPI getTypeAPI();

}