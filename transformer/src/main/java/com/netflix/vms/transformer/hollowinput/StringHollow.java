package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class StringHollow extends HollowObject {

    public StringHollow(StringDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String _getValue() {
        return delegate().getValue(ordinal);
    }

    public boolean _isValueEqual(String testValue) {
        return delegate().isValueEqual(ordinal, testValue);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public StringTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StringDelegate delegate() {
        return (StringDelegate)delegate;
    }

}