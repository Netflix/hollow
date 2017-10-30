package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class CharacterTypeAPI extends HollowObjectTypeAPI {

    private final CharacterDelegateLookupImpl delegateLookupImpl;

    public CharacterTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "characterId",
            "elements",
            "quotes",
            "lastUpdated"
        });
        this.delegateLookupImpl = new CharacterDelegateLookupImpl(this);
    }

    public long getCharacterId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Character", ordinal, "characterId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getCharacterIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Character", ordinal, "characterId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getElementsOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Character", ordinal, "elements");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public CharacterElementsTypeAPI getElementsTypeAPI() {
        return getAPI().getCharacterElementsTypeAPI();
    }

    public int getQuotesOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Character", ordinal, "quotes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public CharacterQuoteListTypeAPI getQuotesTypeAPI() {
        return getAPI().getCharacterQuoteListTypeAPI();
    }

    public long getLastUpdated(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("Character", ordinal, "lastUpdated");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getLastUpdatedBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("Character", ordinal, "lastUpdated");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public CharacterDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}