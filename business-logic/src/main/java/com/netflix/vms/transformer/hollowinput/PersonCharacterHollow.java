package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class PersonCharacterHollow extends HollowObject {

    public PersonCharacterHollow(PersonCharacterDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getPersonId() {
        return delegate().getPersonId(ordinal);
    }

    public Long _getPersonIdBoxed() {
        return delegate().getPersonIdBoxed(ordinal);
    }

    public long _getCharacterId() {
        return delegate().getCharacterId(ordinal);
    }

    public Long _getCharacterIdBoxed() {
        return delegate().getCharacterIdBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonCharacterTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PersonCharacterDelegate delegate() {
        return (PersonCharacterDelegate)delegate;
    }

}