package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CharactersCnTranslatedTextsHollow extends HollowObject {

    public CharactersCnTranslatedTextsHollow(CharactersCnTranslatedTextsDelegate delegate, int ordinal) {
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

    public CharactersCnTranslatedTextsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CharactersCnTranslatedTextsDelegate delegate() {
        return (CharactersCnTranslatedTextsDelegate)delegate;
    }

}