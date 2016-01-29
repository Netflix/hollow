package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class CharacterDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, CharacterDelegate {

    private final Long lastUpdated;
    private final int elementsOrdinal;
    private final Long characterId;
    private final int quotesOrdinal;
   private CharacterTypeAPI typeAPI;

    public CharacterDelegateCachedImpl(CharacterTypeAPI typeAPI, int ordinal) {
        this.lastUpdated = typeAPI.getLastUpdatedBoxed(ordinal);
        this.elementsOrdinal = typeAPI.getElementsOrdinal(ordinal);
        this.characterId = typeAPI.getCharacterIdBoxed(ordinal);
        this.quotesOrdinal = typeAPI.getQuotesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getLastUpdated(int ordinal) {
        return lastUpdated.longValue();
    }

    public Long getLastUpdatedBoxed(int ordinal) {
        return lastUpdated;
    }

    public int getElementsOrdinal(int ordinal) {
        return elementsOrdinal;
    }

    public long getCharacterId(int ordinal) {
        return characterId.longValue();
    }

    public Long getCharacterIdBoxed(int ordinal) {
        return characterId;
    }

    public int getQuotesOrdinal(int ordinal) {
        return quotesOrdinal;
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