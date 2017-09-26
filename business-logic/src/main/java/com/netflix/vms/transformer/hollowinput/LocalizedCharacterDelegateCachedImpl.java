package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class LocalizedCharacterDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, LocalizedCharacterDelegate {

    private final Long characterId;
    private final int translatedTextsOrdinal;
    private final int labelOrdinal;
    private final int attributeNameOrdinal;
    private final int lastUpdatedOrdinal;
    private LocalizedCharacterTypeAPI typeAPI;

    public LocalizedCharacterDelegateCachedImpl(LocalizedCharacterTypeAPI typeAPI, int ordinal) {
        this.characterId = typeAPI.getCharacterIdBoxed(ordinal);
        this.translatedTextsOrdinal = typeAPI.getTranslatedTextsOrdinal(ordinal);
        this.labelOrdinal = typeAPI.getLabelOrdinal(ordinal);
        this.attributeNameOrdinal = typeAPI.getAttributeNameOrdinal(ordinal);
        this.lastUpdatedOrdinal = typeAPI.getLastUpdatedOrdinal(ordinal);
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

    public int getTranslatedTextsOrdinal(int ordinal) {
        return translatedTextsOrdinal;
    }

    public int getLabelOrdinal(int ordinal) {
        return labelOrdinal;
    }

    public int getAttributeNameOrdinal(int ordinal) {
        return attributeNameOrdinal;
    }

    public int getLastUpdatedOrdinal(int ordinal) {
        return lastUpdatedOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public LocalizedCharacterTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (LocalizedCharacterTypeAPI) typeAPI;
    }

}