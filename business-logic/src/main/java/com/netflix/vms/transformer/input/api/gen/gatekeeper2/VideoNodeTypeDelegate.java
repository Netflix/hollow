package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VideoNodeTypeDelegate extends HollowObjectDelegate {

    public String getNodeType(int ordinal);

    public boolean isNodeTypeEqual(int ordinal, String testValue);

    public VideoNodeTypeTypeAPI getTypeAPI();

}