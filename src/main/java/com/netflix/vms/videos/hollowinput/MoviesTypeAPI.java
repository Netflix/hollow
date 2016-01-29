package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class MoviesTypeAPI extends HollowObjectTypeAPI {

    private final MoviesDelegateLookupImpl delegateLookupImpl;

    MoviesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "shortDisplayName",
            "siteSynopsis",
            "originalTitle",
            "displayName",
            "aka",
            "movieId",
            "transliterated",
            "tvSynopsis"
        });
        this.delegateLookupImpl = new MoviesDelegateLookupImpl(this);
    }

    public int getShortDisplayNameOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movies", ordinal, "shortDisplayName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public MoviesShortDisplayNameTypeAPI getShortDisplayNameTypeAPI() {
        return getAPI().getMoviesShortDisplayNameTypeAPI();
    }

    public int getSiteSynopsisOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movies", ordinal, "siteSynopsis");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public MoviesSiteSynopsisTypeAPI getSiteSynopsisTypeAPI() {
        return getAPI().getMoviesSiteSynopsisTypeAPI();
    }

    public int getOriginalTitleOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movies", ordinal, "originalTitle");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public MoviesOriginalTitleTypeAPI getOriginalTitleTypeAPI() {
        return getAPI().getMoviesOriginalTitleTypeAPI();
    }

    public int getDisplayNameOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movies", ordinal, "displayName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public MoviesDisplayNameTypeAPI getDisplayNameTypeAPI() {
        return getAPI().getMoviesDisplayNameTypeAPI();
    }

    public int getAkaOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movies", ordinal, "aka");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public MoviesAkaTypeAPI getAkaTypeAPI() {
        return getAPI().getMoviesAkaTypeAPI();
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleLong("Movies", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[5]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[5] == -1) {
            l = missingDataHandler().handleLong("Movies", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[5]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[5]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getTransliteratedOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movies", ordinal, "transliterated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public MoviesTransliteratedTypeAPI getTransliteratedTypeAPI() {
        return getAPI().getMoviesTransliteratedTypeAPI();
    }

    public int getTvSynopsisOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movies", ordinal, "tvSynopsis");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public MoviesTvSynopsisTypeAPI getTvSynopsisTypeAPI() {
        return getAPI().getMoviesTvSynopsisTypeAPI();
    }

    public MoviesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}