package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class FeedMovieCountryLanguagesTypeAPI extends HollowObjectTypeAPI {

    private final FeedMovieCountryLanguagesDelegateLookupImpl delegateLookupImpl;

    public FeedMovieCountryLanguagesTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieId",
            "countryCode",
            "languageToEarliestWindowStartDateMap"
        });
        this.delegateLookupImpl = new FeedMovieCountryLanguagesDelegateLookupImpl(this);
    }

    public int getMovieIdOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("FeedMovieCountryLanguages", ordinal, "movieId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public LongTypeAPI getMovieIdTypeAPI() {
        return getAPI().getLongTypeAPI();
    }

    public int getCountryCodeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("FeedMovieCountryLanguages", ordinal, "countryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getCountryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getLanguageToEarliestWindowStartDateMapOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("FeedMovieCountryLanguages", ordinal, "languageToEarliestWindowStartDateMap");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public MapOfStringToLongTypeAPI getLanguageToEarliestWindowStartDateMapTypeAPI() {
        return getAPI().getMapOfStringToLongTypeAPI();
    }

    public FeedMovieCountryLanguagesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}