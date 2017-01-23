package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class CharacterArtworkDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, CharacterArtworkDelegate {

    private final Long characterId;
    private final int sourceFileIdOrdinal;
    private final Long seqNum;
    private final int derivativesOrdinal;
    private final int localesOrdinal;
    private final int attributesOrdinal;
    private final Long ordinalPriority;
    private final int fileImageTypeOrdinal;
   private CharacterArtworkTypeAPI typeAPI;

    public CharacterArtworkDelegateCachedImpl(CharacterArtworkTypeAPI typeAPI, int ordinal) {
        this.characterId = typeAPI.getCharacterIdBoxed(ordinal);
        this.sourceFileIdOrdinal = typeAPI.getSourceFileIdOrdinal(ordinal);
        this.seqNum = typeAPI.getSeqNumBoxed(ordinal);
        this.derivativesOrdinal = typeAPI.getDerivativesOrdinal(ordinal);
        this.localesOrdinal = typeAPI.getLocalesOrdinal(ordinal);
        this.attributesOrdinal = typeAPI.getAttributesOrdinal(ordinal);
        this.ordinalPriority = typeAPI.getOrdinalPriorityBoxed(ordinal);
        this.fileImageTypeOrdinal = typeAPI.getFileImageTypeOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getCharacterId(int ordinal) {
        return characterId.longValue();
    }

    public Long getCharacterIdBoxed(int ordinal) {
        return characterId;
    }

    public int getSourceFileIdOrdinal(int ordinal) {
        return sourceFileIdOrdinal;
    }

    public long getSeqNum(int ordinal) {
        return seqNum.longValue();
    }

    public Long getSeqNumBoxed(int ordinal) {
        return seqNum;
    }

    public int getDerivativesOrdinal(int ordinal) {
        return derivativesOrdinal;
    }

    public int getLocalesOrdinal(int ordinal) {
        return localesOrdinal;
    }

    public int getAttributesOrdinal(int ordinal) {
        return attributesOrdinal;
    }

    public long getOrdinalPriority(int ordinal) {
        return ordinalPriority.longValue();
    }

    public Long getOrdinalPriorityBoxed(int ordinal) {
        return ordinalPriority;
    }

    public int getFileImageTypeOrdinal(int ordinal) {
        return fileImageTypeOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public CharacterArtworkTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (CharacterArtworkTypeAPI) typeAPI;
    }

}