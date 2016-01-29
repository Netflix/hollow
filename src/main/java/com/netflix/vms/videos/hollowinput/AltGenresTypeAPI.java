package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class AltGenresTypeAPI extends HollowObjectTypeAPI {

    private final AltGenresDelegateLookupImpl delegateLookupImpl;

    AltGenresTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "alternateNames",
            "displayName",
            "altGenreId",
            "shortName"
        });
        this.delegateLookupImpl = new AltGenresDelegateLookupImpl(this);
    }

    public int getAlternateNamesOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("AltGenres", ordinal, "alternateNames");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public AltGenresArrayOfAlternateNamesTypeAPI getAlternateNamesTypeAPI() {
        return getAPI().getAltGenresArrayOfAlternateNamesTypeAPI();
    }

    public int getDisplayNameOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("AltGenres", ordinal, "displayName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public AltGenresDisplayNameTypeAPI getDisplayNameTypeAPI() {
        return getAPI().getAltGenresDisplayNameTypeAPI();
    }

    public long getAltGenreId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("AltGenres", ordinal, "altGenreId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getAltGenreIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("AltGenres", ordinal, "altGenreId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getShortNameOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("AltGenres", ordinal, "shortName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public AltGenresShortNameTypeAPI getShortNameTypeAPI() {
        return getAPI().getAltGenresShortNameTypeAPI();
    }

    public AltGenresDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}