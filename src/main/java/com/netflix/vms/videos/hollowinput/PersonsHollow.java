package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class PersonsHollow extends HollowObject {

    public PersonsHollow(PersonsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public PersonsNameHollow _getName() {
        int refOrdinal = delegate().getNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPersonsNameHollow(refOrdinal);
    }

    public PersonsBioHollow _getBio() {
        int refOrdinal = delegate().getBioOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPersonsBioHollow(refOrdinal);
    }

    public long _getPersonId() {
        return delegate().getPersonId(ordinal);
    }

    public Long _getPersonIdBoxed() {
        return delegate().getPersonIdBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PersonsDelegate delegate() {
        return (PersonsDelegate)delegate;
    }

}