package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class LocalizedCharacterDelegateLookupImpl extends HollowObjectAbstractDelegate implements LocalizedCharacterDelegate {

    private final LocalizedCharacterTypeAPI typeAPI;

    public LocalizedCharacterDelegateLookupImpl(LocalizedCharacterTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getLastUpdated(int ordinal) {
        return typeAPI.getLastUpdated(ordinal);
    }

    public Long getLastUpdatedBoxed(int ordinal) {
        return typeAPI.getLastUpdatedBoxed(ordinal);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        return typeAPI.getTranslatedTextsOrdinal(ordinal);
    }

    public int getAttributeNameOrdinal(int ordinal) {
        return typeAPI.getAttributeNameOrdinal(ordinal);
    }

    public int getLabelOrdinal(int ordinal) {
        return typeAPI.getLabelOrdinal(ordinal);
    }

    public long getCharacterId(int ordinal) {
        return typeAPI.getCharacterId(ordinal);
    }

    public Long getCharacterIdBoxed(int ordinal) {
        return typeAPI.getCharacterIdBoxed(ordinal);
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