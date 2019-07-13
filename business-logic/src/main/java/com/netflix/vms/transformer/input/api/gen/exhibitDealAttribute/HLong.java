package com.netflix.vms.transformer.input.api.gen.exhibitDealAttribute;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class HLong extends HollowObject {

    public HLong(LongDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getValue() {
        return delegate().getValue(ordinal);
    }

    public Long getValueBoxed() {
        return delegate().getValueBoxed(ordinal);
    }

    public ExhibitDealAttributeV1API api() {
        return typeApi().getAPI();
    }

    public LongTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected LongDelegate delegate() {
        return (LongDelegate)delegate;
    }

}