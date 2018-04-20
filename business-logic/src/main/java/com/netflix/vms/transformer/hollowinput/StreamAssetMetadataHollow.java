package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class StreamAssetMetadataHollow extends HollowObject {

    public StreamAssetMetadataHollow(StreamAssetMetadataDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String _getId() {
        return delegate().getId(ordinal);
    }

    public boolean _isIdEqual(String testValue) {
        return delegate().isIdEqual(ordinal, testValue);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public StreamAssetMetadataTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StreamAssetMetadataDelegate delegate() {
        return (StreamAssetMetadataDelegate)delegate;
    }

}