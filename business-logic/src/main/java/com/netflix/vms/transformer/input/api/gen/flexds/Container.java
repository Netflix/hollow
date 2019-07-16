package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class Container extends HollowObject {

    public Container(ContainerDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public int getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Integer getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public long getParentId() {
        return delegate().getParentId(ordinal);
    }

    public Long getParentIdBoxed() {
        return delegate().getParentIdBoxed(ordinal);
    }

    public long getDataId() {
        return delegate().getDataId(ordinal);
    }

    public Long getDataIdBoxed() {
        return delegate().getDataIdBoxed(ordinal);
    }

    public FlexDSAPI api() {
        return typeApi().getAPI();
    }

    public ContainerTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ContainerDelegate delegate() {
        return (ContainerDelegate)delegate;
    }

}