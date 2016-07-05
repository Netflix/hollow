package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class LocalizedCharacterTypeAPI extends HollowObjectTypeAPI {

    private final LocalizedCharacterDelegateLookupImpl delegateLookupImpl;

    LocalizedCharacterTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "characterId",
            "translatedTexts",
            "label",
            "attributeName",
            "lastUpdated"
        });
        this.delegateLookupImpl = new LocalizedCharacterDelegateLookupImpl(this);
    }

    public long getCharacterId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("LocalizedCharacter", ordinal, "characterId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getCharacterIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("LocalizedCharacter", ordinal, "characterId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("LocalizedCharacter", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public MapOfTranslatedTextTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getMapOfTranslatedTextTypeAPI();
    }

    public int getLabelOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("LocalizedCharacter", ordinal, "label");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getLabelTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getAttributeNameOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("LocalizedCharacter", ordinal, "attributeName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StringTypeAPI getAttributeNameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getLastUpdatedOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("LocalizedCharacter", ordinal, "lastUpdated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public DateTypeAPI getLastUpdatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public LocalizedCharacterDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}