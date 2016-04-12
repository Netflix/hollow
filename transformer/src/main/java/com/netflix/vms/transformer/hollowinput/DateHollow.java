package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class DateHollow extends HollowObject {

    public DateHollow(DateDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getValue() {
        return delegate().getValue(ordinal);
    }

    public Long _getValueBoxed() {
        return delegate().getValueBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public DateTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected DateDelegate delegate() {
        return (DateDelegate)delegate;
    }

}