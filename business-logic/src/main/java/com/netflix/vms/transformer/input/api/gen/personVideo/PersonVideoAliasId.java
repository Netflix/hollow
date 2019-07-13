package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class PersonVideoAliasId extends HollowObject {

    public PersonVideoAliasId(PersonVideoAliasIdDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public int getValue() {
        return delegate().getValue(ordinal);
    }

    public Integer getValueBoxed() {
        return delegate().getValueBoxed(ordinal);
    }

    public PersonVideoAPI api() {
        return typeApi().getAPI();
    }

    public PersonVideoAliasIdTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PersonVideoAliasIdDelegate delegate() {
        return (PersonVideoAliasIdDelegate)delegate;
    }

}