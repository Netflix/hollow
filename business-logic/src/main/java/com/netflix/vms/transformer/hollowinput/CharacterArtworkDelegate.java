package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface CharacterArtworkDelegate extends HollowObjectDelegate {

    public long getCharacterId(int ordinal);

    public Long getCharacterIdBoxed(int ordinal);

    public int getSourceFileIdOrdinal(int ordinal);

    public long getSeqNum(int ordinal);

    public Long getSeqNumBoxed(int ordinal);

    public int getDerivativesOrdinal(int ordinal);

    public int getLocalesOrdinal(int ordinal);

    public int getAttributesOrdinal(int ordinal);

    public long getOrdinalPriority(int ordinal);

    public Long getOrdinalPriorityBoxed(int ordinal);

    public int getFileImageTypeOrdinal(int ordinal);

    public CharacterArtworkTypeAPI getTypeAPI();

}