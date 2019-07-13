package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class DerivativeTag extends HollowObject {

    public DerivativeTag(DerivativeTagDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getValue() {
        return delegate().getValue(ordinal);
    }

    public boolean isValueEqual(String testValue) {
        return delegate().isValueEqual(ordinal, testValue);
    }

    public MceImageV3API api() {
        return typeApi().getAPI();
    }

    public DerivativeTagTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected DerivativeTagDelegate delegate() {
        return (DerivativeTagDelegate)delegate;
    }

}