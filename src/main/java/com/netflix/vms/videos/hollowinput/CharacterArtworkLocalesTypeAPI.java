package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class CharacterArtworkLocalesTypeAPI extends HollowObjectTypeAPI {

    private final CharacterArtworkLocalesDelegateLookupImpl delegateLookupImpl;

    CharacterArtworkLocalesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "territoryCodes",
            "bcp47Code",
            "effectiveDate"
        });
        this.delegateLookupImpl = new CharacterArtworkLocalesDelegateLookupImpl(this);
    }

    public int getTerritoryCodesOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("CharacterArtworkLocales", ordinal, "territoryCodes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public CharacterArtworkLocalesArrayOfTerritoryCodesTypeAPI getTerritoryCodesTypeAPI() {
        return getAPI().getCharacterArtworkLocalesArrayOfTerritoryCodesTypeAPI();
    }

    public int getBcp47CodeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("CharacterArtworkLocales", ordinal, "bcp47Code");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getBcp47CodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getEffectiveDate(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("CharacterArtworkLocales", ordinal, "effectiveDate");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getEffectiveDateBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("CharacterArtworkLocales", ordinal, "effectiveDate");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public CharacterArtworkLocalesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}