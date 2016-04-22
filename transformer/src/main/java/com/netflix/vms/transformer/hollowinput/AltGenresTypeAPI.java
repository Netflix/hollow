package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class AltGenresTypeAPI extends HollowObjectTypeAPI {

    private final AltGenresDelegateLookupImpl delegateLookupImpl;

    AltGenresTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "altGenreId",
            "displayName",
            "shortName",
            "alternateNames"
        });
        this.delegateLookupImpl = new AltGenresDelegateLookupImpl(this);
    }

    public long getAltGenreId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("AltGenres", ordinal, "altGenreId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getAltGenreIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("AltGenres", ordinal, "altGenreId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getDisplayNameOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("AltGenres", ordinal, "displayName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public TranslatedTextTypeAPI getDisplayNameTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getShortNameOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("AltGenres", ordinal, "shortName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public TranslatedTextTypeAPI getShortNameTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getAlternateNamesOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("AltGenres", ordinal, "alternateNames");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public AltGenresAlternateNamesListTypeAPI getAlternateNamesTypeAPI() {
        return getAPI().getAltGenresAlternateNamesListTypeAPI();
    }

    public AltGenresDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}