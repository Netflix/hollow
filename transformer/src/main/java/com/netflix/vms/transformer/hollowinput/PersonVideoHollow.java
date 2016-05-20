package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class PersonVideoHollow extends HollowObject {

    public PersonVideoHollow(PersonVideoDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public PersonVideoAliasIdsListHollow _getAliasIds() {
        int refOrdinal = delegate().getAliasIdsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPersonVideoAliasIdsListHollow(refOrdinal);
    }

    public PersonVideoRolesListHollow _getRoles() {
        int refOrdinal = delegate().getRolesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPersonVideoRolesListHollow(refOrdinal);
    }

    public long _getPersonId() {
        return delegate().getPersonId(ordinal);
    }

    public Long _getPersonIdBoxed() {
        return delegate().getPersonIdBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonVideoTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PersonVideoDelegate delegate() {
        return (PersonVideoDelegate)delegate;
    }

}