package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CharactersHollow extends HollowObject {

    public CharactersHollow(CharactersDelegate delegate, int ordinal) {
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

    public TranslatedTextHollow _getB() {
        int refOrdinal = delegate().getBOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
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

    public CharactersTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CharactersDelegate delegate() {
        return (CharactersDelegate)delegate;
    }

}