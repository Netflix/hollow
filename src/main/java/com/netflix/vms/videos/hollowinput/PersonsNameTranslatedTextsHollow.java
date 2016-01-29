package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class PersonsNameTranslatedTextsHollow extends HollowObject {

    public PersonsNameTranslatedTextsHollow(PersonsNameTranslatedTextsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getValue() {
        int refOrdinal = delegate().getValueOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonsNameTranslatedTextsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PersonsNameTranslatedTextsDelegate delegate() {
        return (PersonsNameTranslatedTextsDelegate)delegate;
    }

}