package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class CharacterArtworkLocaleTypeAPI extends HollowObjectTypeAPI {

    private final CharacterArtworkLocaleDelegateLookupImpl delegateLookupImpl;

    CharacterArtworkLocaleTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "territoryCodes",
            "bcp47Code",
            "effectiveDate"
        });
        this.delegateLookupImpl = new CharacterArtworkLocaleDelegateLookupImpl(this);
    }

    public int getTerritoryCodesOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("CharacterArtworkLocale", ordinal, "territoryCodes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public LocaleTerritoryCodeListTypeAPI getTerritoryCodesTypeAPI() {
        return getAPI().getLocaleTerritoryCodeListTypeAPI();
    }

    public int getBcp47CodeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("CharacterArtworkLocale", ordinal, "bcp47Code");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getBcp47CodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getEffectiveDateOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("CharacterArtworkLocale", ordinal, "effectiveDate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public DateTypeAPI getEffectiveDateTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public CharacterArtworkLocaleDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}