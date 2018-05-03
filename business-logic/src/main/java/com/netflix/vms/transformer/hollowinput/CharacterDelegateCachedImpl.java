package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

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
        if(characterId == null)
            return Long.MIN_VALUE;
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
        if(lastUpdated == null)
            return Long.MIN_VALUE;
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