package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class MovieCountriesTypeAPI extends HollowObjectTypeAPI {

    private final MovieCountriesDelegateLookupImpl delegateLookupImpl;

    public MovieCountriesTypeAPI(OscarAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieId",
            "countries",
            "lastUpdatedCountries",
            "dateCreated",
            "lastUpdated",
            "createdBy",
            "updatedBy"
        });
        this.delegateLookupImpl = new MovieCountriesDelegateLookupImpl(this);
    }

    public int getMovieIdOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieCountries", ordinal, "movieId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public MovieIdTypeAPI getMovieIdTypeAPI() {
        return getAPI().getMovieIdTypeAPI();
    }

    public int getCountriesOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieCountries", ordinal, "countries");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public SetOfStringTypeAPI getCountriesTypeAPI() {
        return getAPI().getSetOfStringTypeAPI();
    }

    public int getLastUpdatedCountriesOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieCountries", ordinal, "lastUpdatedCountries");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public SetOfStringTypeAPI getLastUpdatedCountriesTypeAPI() {
        return getAPI().getSetOfStringTypeAPI();
    }

    public int getDateCreatedOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieCountries", ordinal, "dateCreated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public DateTypeAPI getDateCreatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getLastUpdatedOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieCountries", ordinal, "lastUpdated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public DateTypeAPI getLastUpdatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getCreatedByOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieCountries", ordinal, "createdBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public StringTypeAPI getCreatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getUpdatedByOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieCountries", ordinal, "updatedBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public StringTypeAPI getUpdatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public MovieCountriesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public OscarAPI getAPI() {
        return (OscarAPI) api;
    }

}