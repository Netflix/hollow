package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoNodeType extends HollowObject {

    public VideoNodeType(VideoNodeTypeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getNodeType() {
        return delegate().getNodeType(ordinal);
    }

    public boolean isNodeTypeEqual(String testValue) {
        return delegate().isNodeTypeEqual(ordinal, testValue);
    }

    public Gk2StatusAPI api() {
        return typeApi().getAPI();
    }

    public VideoNodeTypeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoNodeTypeDelegate delegate() {
        return (VideoNodeTypeDelegate)delegate;
    }

}