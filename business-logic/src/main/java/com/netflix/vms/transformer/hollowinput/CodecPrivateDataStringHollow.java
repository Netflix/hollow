package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CodecPrivateDataStringHollow extends HollowObject {

    public CodecPrivateDataStringHollow(CodecPrivateDataStringDelegate delegate, int ordinal) {
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

    public CodecPrivateDataStringTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CodecPrivateDataStringDelegate delegate() {
        return (CodecPrivateDataStringDelegate)delegate;
    }

}