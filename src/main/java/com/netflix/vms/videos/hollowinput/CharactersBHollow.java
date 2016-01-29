package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CharactersBHollow extends HollowObject {

    public CharactersBHollow(CharactersBDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public CharactersBMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCharactersBMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CharactersBTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CharactersBDelegate delegate() {
        return (CharactersBDelegate)delegate;
    }

}