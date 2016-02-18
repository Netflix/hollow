package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CharacterArtworkHollow extends HollowObject {

    public CharacterArtworkHollow(CharacterArtworkDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getCharacterId() {
        return delegate().getCharacterId(ordinal);
    }

    public Long _getCharacterIdBoxed() {
        return delegate().getCharacterIdBoxed(ordinal);
    }

    public StringHollow _getSourceFileId() {
        int refOrdinal = delegate().getSourceFileIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getSeqNum() {
        return delegate().getSeqNum(ordinal);
    }

    public Long _getSeqNumBoxed() {
        return delegate().getSeqNumBoxed(ordinal);
    }

    public CharacterArtworkDerivativeListHollow _getDerivatives() {
        int refOrdinal = delegate().getDerivativesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCharacterArtworkDerivativeListHollow(refOrdinal);
    }

    public CharacterArtworkLocaleListHollow _getLocales() {
        int refOrdinal = delegate().getLocalesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCharacterArtworkLocaleListHollow(refOrdinal);
    }

    public CharacterArtworkAttributesHollow _getAttributes() {
        int refOrdinal = delegate().getAttributesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCharacterArtworkAttributesHollow(refOrdinal);
    }

    public long _getOrdinalPriority() {
        return delegate().getOrdinalPriority(ordinal);
    }

    public Long _getOrdinalPriorityBoxed() {
        return delegate().getOrdinalPriorityBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CharacterArtworkTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CharacterArtworkDelegate delegate() {
        return (CharacterArtworkDelegate)delegate;
    }

}