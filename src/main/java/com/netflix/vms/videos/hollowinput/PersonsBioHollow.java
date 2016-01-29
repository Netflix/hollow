package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class PersonsBioHollow extends HollowObject {

    public PersonsBioHollow(PersonsBioDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public PersonsBioMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPersonsBioMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonsBioTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PersonsBioDelegate delegate() {
        return (PersonsBioDelegate)delegate;
    }

}