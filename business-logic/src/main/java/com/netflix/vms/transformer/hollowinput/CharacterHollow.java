package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class CharacterHollow extends HollowObject {

    public CharacterHollow(CharacterDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getCharacterId() {
        return delegate().getCharacterId(ordinal);
    }

    public Long _getCharacterIdBoxed() {
        return delegate().getCharacterIdBoxed(ordinal);
    }

    public CharacterElementsHollow _getElements() {
        int refOrdinal = delegate().getElementsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCharacterElementsHollow(refOrdinal);
    }

    public CharacterQuoteListHollow _getQuotes() {
        int refOrdinal = delegate().getQuotesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCharacterQuoteListHollow(refOrdinal);
    }

    public long _getLastUpdated() {
        return delegate().getLastUpdated(ordinal);
    }

    public Long _getLastUpdatedBoxed() {
        return delegate().getLastUpdatedBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public CharacterTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CharacterDelegate delegate() {
        return (CharacterDelegate)delegate;
    }

}