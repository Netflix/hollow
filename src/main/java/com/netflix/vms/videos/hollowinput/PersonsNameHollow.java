package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class PersonsNameHollow extends HollowObject {

    public PersonsNameHollow(PersonsNameDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public PersonsNameMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPersonsNameMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonsNameTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PersonsNameDelegate delegate() {
        return (PersonsNameDelegate)delegate;
    }

}