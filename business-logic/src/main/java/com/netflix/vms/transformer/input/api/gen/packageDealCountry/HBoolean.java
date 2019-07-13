package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class HBoolean extends HollowObject {

    public HBoolean(BooleanDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public boolean getValue() {
        return delegate().getValue(ordinal);
    }

    public Boolean getValueBoxed() {
        return delegate().getValueBoxed(ordinal);
    }

    public PackageDealCountryAPI api() {
        return typeApi().getAPI();
    }

    public BooleanTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected BooleanDelegate delegate() {
        return (BooleanDelegate)delegate;
    }

}