package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CharacterElementsHollow extends HollowObject {

    public CharacterElementsHollow(CharacterElementsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getCharacter_Name() {
        int refOrdinal = delegate().getCharacter_NameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getBlade_Bottom_Line() {
        int refOrdinal = delegate().getBlade_Bottom_LineOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getCharacter_Bio() {
        int refOrdinal = delegate().getCharacter_BioOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getBlade_Top_Line() {
        int refOrdinal = delegate().getBlade_Top_LineOrdinal(ordinal);
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