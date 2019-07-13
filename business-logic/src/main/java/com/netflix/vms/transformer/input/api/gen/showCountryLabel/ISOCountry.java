package com.netflix.vms.transformer.input.api.gen.showCountryLabel;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class ISOCountry extends HollowObject {

    public ISOCountry(ISOCountryDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getValue() {
        return delegate().getValue(ordinal);
    }

    public boolean isValueEqual(String testValue) {
        return delegate().isValueEqual(ordinal, testValue);
    }

    public ShowCountryLabelAPI api() {
        return typeApi().getAPI();
    }

    public ISOCountryTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ISOCountryDelegate delegate() {
        return (ISOCountryDelegate)delegate;
    }

}