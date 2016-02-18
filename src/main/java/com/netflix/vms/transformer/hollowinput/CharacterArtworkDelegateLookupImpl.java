package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class CharacterArtworkDelegateLookupImpl extends HollowObjectAbstractDelegate implements CharacterArtworkDelegate {

    private final CharacterArtworkTypeAPI typeAPI;

    public CharacterArtworkDelegateLookupImpl(CharacterArtworkTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getCharacterId(int ordinal) {
        return typeAPI.getCharacterId(ordinal);
    }

    public Long getCharacterIdBoxed(int ordinal) {
        return typeAPI.getCharacterIdBoxed(ordinal);
    }

    public int getSourceFileIdOrdinal(int ordinal) {
        return typeAPI.getSourceFileIdOrdinal(ordinal);
    }

    public long getSeqNum(int ordinal) {
        return typeAPI.getSeqNum(ordinal);
    }

    public Long getSeqNumBoxed(int ordinal) {
        return typeAPI.getSeqNumBoxed(ordinal);
    }

    public int getDerivativesOrdinal(int ordinal) {
        return typeAPI.getDerivativesOrdinal(ordinal);
    }

    public int getLocalesOrdinal(int ordinal) {
        return typeAPI.getLocalesOrdinal(ordinal);
    }

    public int getAttributesOrdinal(int ordinal) {
        return typeAPI.getAttributesOrdinal(ordinal);
    }

    public long getOrdinalPriority(int ordinal) {
        return typeAPI.getOrdinalPriority(ordinal);
    }

    public Long getOrdinalPriorityBoxed(int ordinal) {
        return typeAPI.getOrdinalPriorityBoxed(ordinal);
    }

    public CharacterArtworkTypeAPI getTypeAPI() {
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