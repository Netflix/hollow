package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface CharacterArtworkSourceDelegate extends HollowObjectDelegate {

    public int getSourceFileIdOrdinal(int ordinal);

    public long getCharacterId(int ordinal);

    public Long getCharacterIdBoxed(int ordinal);

    public boolean getIsFallback(int ordinal);

    public Boolean getIsFallbackBoxed(int ordinal);

    public int getFallbackSourceFileIdOrdinal(int ordinal);

    public int getSeqNum(int ordinal);

    public Integer getSeqNumBoxed(int ordinal);

    public int getOrdinalPriority(int ordinal);

    public Integer getOrdinalPriorityBoxed(int ordinal);

    public int getFileImageTypeOrdinal(int ordinal);

    public int getPhaseTagsOrdinal(int ordinal);

    public boolean getIsSmoky(int ordinal);

    public Boolean getIsSmokyBoxed(int ordinal);

    public boolean getRolloutExclusive(int ordinal);

    public Boolean getRolloutExclusiveBoxed(int ordinal);

    public int getAttributesOrdinal(int ordinal);

    public int getLocalesOrdinal(int ordinal);

    public CharacterArtworkSourceTypeAPI getTypeAPI();

}