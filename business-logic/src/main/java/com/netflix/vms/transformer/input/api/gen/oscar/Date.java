package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class Date extends HollowObject {

    public Date(DateDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getValue() {
        return delegate().getValue(ordinal);
    }

    public Long getValueBoxed() {
        return delegate().getValueBoxed(ordinal);
    }

    public OscarAPI api() {
        return typeApi().getAPI();
    }

    public DateTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected DateDelegate delegate() {
        return (DateDelegate)delegate;
    }

}