package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class CharacterArtworkSourceDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, CharacterArtworkSourceDelegate {

    private final int sourceFileIdOrdinal;
    private final Long characterId;
    private final Boolean isFallback;
    private final int fallbackSourceFileIdOrdinal;
    private final Integer seqNum;
    private final Integer ordinalPriority;
    private final int fileImageTypeOrdinal;
    private final int phaseTagsOrdinal;
    private final Boolean isSmoky;
    private final Boolean rolloutExclusive;
    private final int attributesOrdinal;
    private final int localesOrdinal;
   private CharacterArtworkSourceTypeAPI typeAPI;

    public CharacterArtworkSourceDelegateCachedImpl(CharacterArtworkSourceTypeAPI typeAPI, int ordinal) {
        this.sourceFileIdOrdinal = typeAPI.getSourceFileIdOrdinal(ordinal);
        this.characterId = typeAPI.getCharacterIdBoxed(ordinal);
        this.isFallback = typeAPI.getIsFallbackBoxed(ordinal);
        this.fallbackSourceFileIdOrdinal = typeAPI.getFallbackSourceFileIdOrdinal(ordinal);
        this.seqNum = typeAPI.getSeqNumBoxed(ordinal);
        this.ordinalPriority = typeAPI.getOrdinalPriorityBoxed(ordinal);
        this.fileImageTypeOrdinal = typeAPI.getFileImageTypeOrdinal(ordinal);
        this.phaseTagsOrdinal = typeAPI.getPhaseTagsOrdinal(ordinal);
        this.isSmoky = typeAPI.getIsSmokyBoxed(ordinal);
        this.rolloutExclusive = typeAPI.getRolloutExclusiveBoxed(ordinal);
        this.attributesOrdinal = typeAPI.getAttributesOrdinal(ordinal);
        this.localesOrdinal = typeAPI.getLocalesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getSourceFileIdOrdinal(int ordinal) {
        return sourceFileIdOrdinal;
    }

    public long getCharacterId(int ordinal) {
        return characterId.longValue();
    }

    public Long getCharacterIdBoxed(int ordinal) {
        return characterId;
    }

    public boolean getIsFallback(int ordinal) {
        return isFallback.booleanValue();
    }

    public Boolean getIsFallbackBoxed(int ordinal) {
        return isFallback;
    }

    public int getFallbackSourceFileIdOrdinal(int ordinal) {
        return fallbackSourceFileIdOrdinal;
    }

    public int getSeqNum(int ordinal) {
        return seqNum.intValue();
    }

    public Integer getSeqNumBoxed(int ordinal) {
        return seqNum;
    }

    public int getOrdinalPriority(int ordinal) {
        return ordinalPriority.intValue();
    }

    public Integer getOrdinalPriorityBoxed(int ordinal) {
        return ordinalPriority;
    }

    public int getFileImageTypeOrdinal(int ordinal) {
        return fileImageTypeOrdinal;
    }

    public int getPhaseTagsOrdinal(int ordinal) {
        return phaseTagsOrdinal;
    }

    public boolean getIsSmoky(int ordinal) {
        return isSmoky.booleanValue();
    }

    public Boolean getIsSmokyBoxed(int ordinal) {
        return isSmoky;
    }

    public boolean getRolloutExclusive(int ordinal) {
        return rolloutExclusive.booleanValue();
    }

    public Boolean getRolloutExclusiveBoxed(int ordinal) {
        return rolloutExclusive;
    }

    public int getAttributesOrdinal(int ordinal) {
        return attributesOrdinal;
    }

    public int getLocalesOrdinal(int ordinal) {
        return localesOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public CharacterArtworkSourceTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (CharacterArtworkSourceTypeAPI) typeAPI;
    }

}