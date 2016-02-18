package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CharacterElementsHollow extends HollowObject {

    public CharacterElementsHollow(CharacterElementsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getCharacterName() {
        int refOrdinal = delegate().getCharacterNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getBladeBottomLine() {
        int refOrdinal = delegate().getBladeBottomLineOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getCharacterBio() {
        int refOrdinal = delegate().getCharacterBioOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getBladeTopLine() {
        int refOrdinal = delegate().getBladeTopLineOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CharacterElementsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CharacterElementsDelegate delegate() {
        return (CharacterElementsDelegate)delegate;
    }

}