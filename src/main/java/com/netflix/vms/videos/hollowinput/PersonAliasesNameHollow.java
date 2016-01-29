package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class PersonAliasesNameHollow extends HollowObject {

    public PersonAliasesNameHollow(PersonAliasesNameDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public PersonAliasesNameMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPersonAliasesNameMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonAliasesNameTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PersonAliasesNameDelegate delegate() {
        return (PersonAliasesNameDelegate)delegate;
    }

}