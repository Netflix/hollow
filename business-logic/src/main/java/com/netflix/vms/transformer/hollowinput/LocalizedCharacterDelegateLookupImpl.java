package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class LocalizedCharacterDelegateLookupImpl extends HollowObjectAbstractDelegate implements LocalizedCharacterDelegate {

    private final LocalizedCharacterTypeAPI typeAPI;

    public LocalizedCharacterDelegateLookupImpl(LocalizedCharacterTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getCharacterId(int ordinal) {
        return typeAPI.getCharacterId(ordinal);
    }

    public Long getCharacterIdBoxed(int ordinal) {
        return typeAPI.getCharacterIdBoxed(ordinal);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        return typeAPI.getTranslatedTextsOrdinal(ordinal);
    }

    public int getLabelOrdinal(int ordinal) {
        return typeAPI.getLabelOrdinal(ordinal);
    }

    public int getAttributeNameOrdinal(int ordinal) {
        return typeAPI.getAttributeNameOrdinal(ordinal);
    }

    public int getLastUpdatedOrdinal(int ordinal) {
        return typeAPI.getLastUpdatedOrdinal(ordinal);
    }

    public LocalizedCharacterTypeAPI getTypeAPI() {
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