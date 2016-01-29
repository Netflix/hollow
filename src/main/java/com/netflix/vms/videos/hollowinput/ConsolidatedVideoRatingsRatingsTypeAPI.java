package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class ConsolidatedVideoRatingsRatingsTypeAPI extends HollowObjectTypeAPI {

    private final ConsolidatedVideoRatingsRatingsDelegateLookupImpl delegateLookupImpl;

    ConsolidatedVideoRatingsRatingsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "countryRatings",
            "countryList"
        });
        this.delegateLookupImpl = new ConsolidatedVideoRatingsRatingsDelegateLookupImpl(this);
    }

    public int getCountryRatingsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedVideoRatingsRatings", ordinal, "countryRatings");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public ConsolidatedVideoRatingsRatingsArrayOfCountryRatingsTypeAPI getCountryRatingsTypeAPI() {
        return getAPI().getConsolidatedVideoRatingsRatingsArrayOfCountryRatingsTypeAPI();
    }

    public int getCountryListOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedVideoRatingsRatings", ordinal, "countryList");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public ConsolidatedVideoRatingsRatingsArrayOfCountryListTypeAPI getCountryListTypeAPI() {
        return getAPI().getConsolidatedVideoRatingsRatingsArrayOfCountryListTypeAPI();
    }

    public ConsolidatedVideoRatingsRatingsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}