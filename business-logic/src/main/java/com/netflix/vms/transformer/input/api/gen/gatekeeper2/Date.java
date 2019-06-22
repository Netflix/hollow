package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.HollowObject;

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

    public Gk2StatusAPI api() {
        return typeApi().getAPI();
    }

    public DateTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected DateDelegate delegate() {
        return (DateDelegate)delegate;
    }

}