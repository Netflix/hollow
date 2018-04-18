package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class DerivativeTagHollow extends HollowObject {

    public DerivativeTagHollow(DerivativeTagDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String _getValue() {
        return delegate().getValue(ordinal);
    }

    public boolean _isValueEqual(String testValue) {
        return delegate().isValueEqual(ordinal, testValue);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public DerivativeTagTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected DerivativeTagDelegate delegate() {
        return (DerivativeTagDelegate)delegate;
    }

}