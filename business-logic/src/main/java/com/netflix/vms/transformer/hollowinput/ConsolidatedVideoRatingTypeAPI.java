package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class ConsolidatedVideoRatingTypeAPI extends HollowObjectTypeAPI {

    private final ConsolidatedVideoRatingDelegateLookupImpl delegateLookupImpl;

    ConsolidatedVideoRatingTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "countryRatings",
            "countryList"
        });
        this.delegateLookupImpl = new ConsolidatedVideoRatingDelegateLookupImpl(this);
    }

    public int getCountryRatingsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedVideoRating", ordinal, "countryRatings");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public ConsolidatedVideoCountryRatingListTypeAPI getCountryRatingsTypeAPI() {
        return getAPI().getConsolidatedVideoCountryRatingListTypeAPI();
    }

    public int getCountryListOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedVideoRating", ordinal, "countryList");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public ISOCountryListTypeAPI getCountryListTypeAPI() {
        return getAPI().getISOCountryListTypeAPI();
    }

    public ConsolidatedVideoRatingDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}