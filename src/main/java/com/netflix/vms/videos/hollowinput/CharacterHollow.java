package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CharacterHollow extends HollowObject {

    public CharacterHollow(CharacterDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getLastUpdated() {
        return delegate().getLastUpdated(ordinal);
    }

    public Long _getLastUpdatedBoxed() {
        return delegate().getLastUpdatedBoxed(ordinal);
    }

    public CharacterElementsHollow _getElements() {
        int refOrdinal = delegate().getElementsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCharacterElementsHollow(refOrdinal);
    }

    public long _getCharacterId() {
        return delegate().getCharacterId(ordinal);
    }

    public Long _getCharacterIdBoxed() {
        return delegate().getCharacterIdBoxed(ordinal);
    }

    public CharacterArrayOfQuotesHollow _getQuotes() {
        int refOrdinal = delegate().getQuotesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCharacterArrayOfQuotesHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CharacterTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CharacterDelegate delegate() {
        return (CharacterDelegate)delegate;
    }

}