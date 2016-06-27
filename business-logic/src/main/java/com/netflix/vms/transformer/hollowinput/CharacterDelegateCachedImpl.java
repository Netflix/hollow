package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class CharacterDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, CharacterDelegate {

    private final Long characterId;
    private final int elementsOrdinal;
    private final int quotesOrdinal;
    private final Long lastUpdated;
   private CharacterTypeAPI typeAPI;

    public CharacterDelegateCachedImpl(CharacterTypeAPI typeAPI, int ordinal) {
        this.characterId = typeAPI.getCharacterIdBoxed(ordinal);
        this.elementsOrdinal = typeAPI.getElementsOrdinal(ordinal);
        this.quotesOrdinal = typeAPI.getQuotesOrdinal(ordinal);
        this.lastUpdated = typeAPI.getLastUpdatedBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getCharacterId(int ordinal) {
        return characterId.longValue();
    }

    public Long getCharacterIdBoxed(int ordinal) {
        return characterId;
    }

    public int getElementsOrdinal(int ordinal) {
        return elementsOrdinal;
    }

    public int getQuotesOrdinal(int ordinal) {
        return quotesOrdinal;
    }

    public long getLastUpdated(int ordinal) {
        return lastUpdated.longValue();
    }

    public Long getLastUpdatedBoxed(int ordinal) {
        return lastUpdated;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public CharacterTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (CharacterTypeAPI) typeAPI;
    }

}