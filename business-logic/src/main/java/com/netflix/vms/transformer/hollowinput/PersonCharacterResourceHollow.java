package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class PersonCharacterResourceHollow extends HollowObject {

    public PersonCharacterResourceHollow(PersonCharacterResourceDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getId() {
        return delegate().getId(ordinal);
    }

    public Long _getIdBoxed() {
        return delegate().getIdBoxed(ordinal);
    }

    public StringHollow _getPrefix() {
        int refOrdinal = delegate().getPrefixOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public TranslatedTextHollow _getCn() {
        int refOrdinal = delegate().getCnOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonCharacterResourceTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PersonCharacterResourceDelegate delegate() {
        return (PersonCharacterResourceDelegate)delegate;
    }

}