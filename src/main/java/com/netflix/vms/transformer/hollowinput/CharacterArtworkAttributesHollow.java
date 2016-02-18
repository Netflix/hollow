package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CharacterArtworkAttributesHollow extends HollowObject {

    public CharacterArtworkAttributesHollow(CharacterArtworkAttributesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getFile_seq() {
        int refOrdinal = delegate().getFile_seqOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CharacterArtworkAttributesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CharacterArtworkAttributesDelegate delegate() {
        return (CharacterArtworkAttributesDelegate)delegate;
    }

}