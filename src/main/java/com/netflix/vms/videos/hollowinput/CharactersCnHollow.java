package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CharactersCnHollow extends HollowObject {

    public CharactersCnHollow(CharactersCnDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public CharactersCnMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCharactersCnMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CharactersCnTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CharactersCnDelegate delegate() {
        return (CharactersCnDelegate)delegate;
    }

}