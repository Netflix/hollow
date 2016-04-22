package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class DrmInfoStringHollow extends HollowObject {

    public DrmInfoStringHollow(DrmInfoStringDelegate delegate, int ordinal) {
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

    public DrmInfoStringTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected DrmInfoStringDelegate delegate() {
        return (DrmInfoStringDelegate)delegate;
    }

}