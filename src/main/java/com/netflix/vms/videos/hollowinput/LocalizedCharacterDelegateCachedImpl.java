package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class LocalizedCharacterDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, LocalizedCharacterDelegate {

    private final Long lastUpdated;
    private final int translatedTextsOrdinal;
    private final int attributeNameOrdinal;
    private final int labelOrdinal;
    private final Long characterId;
   private LocalizedCharacterTypeAPI typeAPI;

    public LocalizedCharacterDelegateCachedImpl(LocalizedCharacterTypeAPI typeAPI, int ordinal) {
        this.lastUpdated = typeAPI.getLastUpdatedBoxed(ordinal);
        this.translatedTextsOrdinal = typeAPI.getTranslatedTextsOrdinal(ordinal);
        this.attributeNameOrdinal = typeAPI.getAttributeNameOrdinal(ordinal);
        this.labelOrdinal = typeAPI.getLabelOrdinal(ordinal);
        this.characterId = typeAPI.getCharacterIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getLastUpdated(int ordinal) {
        return lastUpdated.longValue();
    }

    public Long getLastUpdatedBoxed(int ordinal) {
        return lastUpdated;
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        return translatedTextsOrdinal;
    }

    public int getAttributeNameOrdinal(int ordinal) {
        return attributeNameOrdinal;
    }

    public int getLabelOrdinal(int ordinal) {
        return labelOrdinal;
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

    public LocalizedCharacterTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (LocalizedCharacterTypeAPI) typeAPI;
    }

}