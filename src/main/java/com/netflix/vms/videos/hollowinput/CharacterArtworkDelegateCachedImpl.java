package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class CharacterArtworkDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, CharacterArtworkDelegate {

    private final int derivativesOrdinal;
    private final int localesOrdinal;
    private final Long seqNum;
    private final Long ordinalPriority;
    private final int sourceFileIdOrdinal;
    private final int attributesOrdinal;
    private final Long characterId;
   private CharacterArtworkTypeAPI typeAPI;

    public CharacterArtworkDelegateCachedImpl(CharacterArtworkTypeAPI typeAPI, int ordinal) {
        this.derivativesOrdinal = typeAPI.getDerivativesOrdinal(ordinal);
        this.localesOrdinal = typeAPI.getLocalesOrdinal(ordinal);
        this.seqNum = typeAPI.getSeqNumBoxed(ordinal);
        this.ordinalPriority = typeAPI.getOrdinalPriorityBoxed(ordinal);
        this.sourceFileIdOrdinal = typeAPI.getSourceFileIdOrdinal(ordinal);
        this.attributesOrdinal = typeAPI.getAttributesOrdinal(ordinal);
        this.characterId = typeAPI.getCharacterIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getDerivativesOrdinal(int ordinal) {
        return derivativesOrdinal;
    }

    public int getLocalesOrdinal(int ordinal) {
        return localesOrdinal;
    }

    public long getSeqNum(int ordinal) {
        return seqNum.longValue();
    }

    public Long getSeqNumBoxed(int ordinal) {
        return seqNum;
    }

    public long getOrdinalPriority(int ordinal) {
        return ordinalPriority.longValue();
    }

    public Long getOrdinalPriorityBoxed(int ordinal) {
        return ordinalPriority;
    }

    public int getSourceFileIdOrdinal(int ordinal) {
        return sourceFileIdOrdinal;
    }

    public int getAttributesOrdinal(int ordinal) {
        return attributesOrdinal;
    }

    public long getCharacterId(int ordinal) {
        return characterId.longValue();
    }

    public Long getCharacterIdBoxed(int ordinal) {
        return characterId;
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