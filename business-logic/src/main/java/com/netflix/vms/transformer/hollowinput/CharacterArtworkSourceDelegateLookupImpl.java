package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CharacterArtworkSourceDelegateLookupImpl extends HollowObjectAbstractDelegate implements CharacterArtworkSourceDelegate {

    private final CharacterArtworkSourceTypeAPI typeAPI;

    public CharacterArtworkSourceDelegateLookupImpl(CharacterArtworkSourceTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getSourceFileIdOrdinal(int ordinal) {
        return typeAPI.getSourceFileIdOrdinal(ordinal);
    }

    public long getCharacterId(int ordinal) {
        return typeAPI.getCharacterId(ordinal);
    }

    public Long getCharacterIdBoxed(int ordinal) {
        return typeAPI.getCharacterIdBoxed(ordinal);
    }

    public boolean getIsFallback(int ordinal) {
        return typeAPI.getIsFallback(ordinal);
    }

    public Boolean getIsFallbackBoxed(int ordinal) {
        return typeAPI.getIsFallbackBoxed(ordinal);
    }

    public int getFallbackSourceFileIdOrdinal(int ordinal) {
        return typeAPI.getFallbackSourceFileIdOrdinal(ordinal);
    }

    public int getSeqNum(int ordinal) {
        return typeAPI.getSeqNum(ordinal);
    }

    public Integer getSeqNumBoxed(int ordinal) {
        return typeAPI.getSeqNumBoxed(ordinal);
    }

    public int getOrdinalPriority(int ordinal) {
        return typeAPI.getOrdinalPriority(ordinal);
    }

    public Integer getOrdinalPriorityBoxed(int ordinal) {
        return typeAPI.getOrdinalPriorityBoxed(ordinal);
    }

    public int getFileImageTypeOrdinal(int ordinal) {
        return typeAPI.getFileImageTypeOrdinal(ordinal);
    }

    public int getPhaseTagsOrdinal(int ordinal) {
        return typeAPI.getPhaseTagsOrdinal(ordinal);
    }

    public boolean getIsSmoky(int ordinal) {
        return typeAPI.getIsSmoky(ordinal);
    }

    public Boolean getIsSmokyBoxed(int ordinal) {
        return typeAPI.getIsSmokyBoxed(ordinal);
    }

    public boolean getRolloutExclusive(int ordinal) {
        return typeAPI.getRolloutExclusive(ordinal);
    }

    public Boolean getRolloutExclusiveBoxed(int ordinal) {
        return typeAPI.getRolloutExclusiveBoxed(ordinal);
    }

    public int getAttributesOrdinal(int ordinal) {
        return typeAPI.getAttributesOrdinal(ordinal);
    }

    public int getLocalesOrdinal(int ordinal) {
        return typeAPI.getLocalesOrdinal(ordinal);
    }

    public CharacterArtworkSourceTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}