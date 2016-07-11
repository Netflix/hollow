package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class PersonBioHollow extends HollowObject {

    public PersonBioHollow(PersonBioDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public ListOfStringHollow _getSpouses() {
        int refOrdinal = delegate().getSpousesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfStringHollow(refOrdinal);
    }

    public ListOfStringHollow _getPartners() {
        int refOrdinal = delegate().getPartnersOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfStringHollow(refOrdinal);
    }

    public StringHollow _getCurrentRelationship() {
        int refOrdinal = delegate().getCurrentRelationshipOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getPersonId() {
        return delegate().getPersonId(ordinal);
    }

    public Long _getPersonIdBoxed() {
        return delegate().getPersonIdBoxed(ordinal);
    }

    public ExplicitDateHollow _getBirthDate() {
        int refOrdinal = delegate().getBirthDateOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getExplicitDateHollow(refOrdinal);
    }

    public ListOfVideoIdsHollow _getMovieIds() {
        int refOrdinal = delegate().getMovieIdsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfVideoIdsHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonBioTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PersonBioDelegate delegate() {
        return (PersonBioDelegate)delegate;
    }

}