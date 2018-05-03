package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class PersonVideoAliasIdHollow extends HollowObject {

    public PersonVideoAliasIdHollow(PersonVideoAliasIdDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public int _getValue() {
        return delegate().getValue(ordinal);
    }

    public Integer _getValueBoxed() {
        return delegate().getValueBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonVideoAliasIdTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PersonVideoAliasIdDelegate delegate() {
        return (PersonVideoAliasIdDelegate)delegate;
    }

}