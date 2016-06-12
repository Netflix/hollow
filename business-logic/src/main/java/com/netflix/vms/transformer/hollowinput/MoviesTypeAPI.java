package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class MoviesTypeAPI extends HollowObjectTypeAPI {

    private final MoviesDelegateLookupImpl delegateLookupImpl;

    MoviesTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieId",
            "shortDisplayName",
            "siteSynopsis",
            "originalTitle",
            "displayName",
            "aka",
            "transliterated",
            "tvSynopsis"
        });
        this.delegateLookupImpl = new MoviesDelegateLookupImpl(this);
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Movies", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Movies", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getShortDisplayNameOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movies", ordinal, "shortDisplayName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public TranslatedTextTypeAPI getShortDisplayNameTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSiteSynopsisOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movies", ordinal, "siteSynopsis");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public TranslatedTextTypeAPI getSiteSynopsisTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getOriginalTitleOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movies", ordinal, "originalTitle");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public TranslatedTextTypeAPI getOriginalTitleTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getDisplayNameOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movies", ordinal, "displayName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public TranslatedTextTypeAPI getDisplayNameTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getAkaOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movies", ordinal, "aka");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public TranslatedTextTypeAPI getAkaTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getTransliteratedOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movies", ordinal, "transliterated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public TranslatedTextTypeAPI getTransliteratedTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getTvSynopsisOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movies", ordinal, "tvSynopsis");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public TranslatedTextTypeAPI getTvSynopsisTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public MoviesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}