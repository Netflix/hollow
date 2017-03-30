package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CharacterArtworkSourceHollow extends HollowObject {

    public CharacterArtworkSourceHollow(CharacterArtworkSourceDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getSourceFileId() {
        int refOrdinal = delegate().getSourceFileIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getCharacterId() {
        return delegate().getCharacterId(ordinal);
    }

    public Long _getCharacterIdBoxed() {
        return delegate().getCharacterIdBoxed(ordinal);
    }

    public boolean _getIsFallback() {
        return delegate().getIsFallback(ordinal);
    }

    public Boolean _getIsFallbackBoxed() {
        return delegate().getIsFallbackBoxed(ordinal);
    }

    public StringHollow _getFallbackSourceFileId() {
        int refOrdinal = delegate().getFallbackSourceFileIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public int _getSeqNum() {
        return delegate().getSeqNum(ordinal);
    }

    public Integer _getSeqNumBoxed() {
        return delegate().getSeqNumBoxed(ordinal);
    }

    public int _getOrdinalPriority() {
        return delegate().getOrdinalPriority(ordinal);
    }

    public Integer _getOrdinalPriorityBoxed() {
        return delegate().getOrdinalPriorityBoxed(ordinal);
    }

    public StringHollow _getFileImageType() {
        int refOrdinal = delegate().getFileImageTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public PhaseTagListHollow _getPhaseTags() {
        int refOrdinal = delegate().getPhaseTagsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPhaseTagListHollow(refOrdinal);
    }

    public boolean _getIsSmoky() {
        return delegate().getIsSmoky(ordinal);
    }

    public Boolean _getIsSmokyBoxed() {
        return delegate().getIsSmokyBoxed(ordinal);
    }

    public boolean _getRolloutExclusive() {
        return delegate().getRolloutExclusive(ordinal);
    }

    public Boolean _getRolloutExclusiveBoxed() {
        return delegate().getRolloutExclusiveBoxed(ordinal);
    }

    public ArtworkAttributesHollow _getAttributes() {
        int refOrdinal = delegate().getAttributesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getArtworkAttributesHollow(refOrdinal);
    }

    public ArtworkLocaleListHollow _getLocales() {
        int refOrdinal = delegate().getLocalesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getArtworkLocaleListHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public CharacterArtworkSourceTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CharacterArtworkSourceDelegate delegate() {
        return (CharacterArtworkSourceDelegate)delegate;
    }

}