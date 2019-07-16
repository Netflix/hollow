package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ParentNodeId extends HollowObject {

    public ParentNodeId(ParentNodeIdDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getValue() {
        return delegate().getValue(ordinal);
    }

    public Long getValueBoxed() {
        return delegate().getValueBoxed(ordinal);
    }

    public Gk2StatusAPI api() {
        return typeApi().getAPI();
    }

    public ParentNodeIdTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ParentNodeIdDelegate delegate() {
        return (ParentNodeIdDelegate)delegate;
    }

}