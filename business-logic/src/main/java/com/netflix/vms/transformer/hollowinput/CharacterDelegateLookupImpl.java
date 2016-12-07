package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CharacterDelegateLookupImpl extends HollowObjectAbstractDelegate implements CharacterDelegate {

    private final CharacterTypeAPI typeAPI;

    public CharacterDelegateLookupImpl(CharacterTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getCharacterId(int ordinal) {
        return typeAPI.getCharacterId(ordinal);
    }

    public Long getCharacterIdBoxed(int ordinal) {
        return typeAPI.getCharacterIdBoxed(ordinal);
    }

    public int getElementsOrdinal(int ordinal) {
        return typeAPI.getElementsOrdinal(ordinal);
    }

    public int getQuotesOrdinal(int ordinal) {
        return typeAPI.getQuotesOrdinal(ordinal);
    }

    public long getLastUpdated(int ordinal) {
        return typeAPI.getLastUpdated(ordinal);
    }

    public Long getLastUpdatedBoxed(int ordinal) {
        return typeAPI.getLastUpdatedBoxed(ordinal);
    }

    public CharacterTypeAPI getTypeAPI() {
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