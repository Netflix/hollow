package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface StreamAssetMetadataDelegate extends HollowObjectDelegate {

    public String getId(int ordinal);

    public boolean isIdEqual(int ordinal, String testValue);

    public StreamAssetMetadataTypeAPI getTypeAPI();

}