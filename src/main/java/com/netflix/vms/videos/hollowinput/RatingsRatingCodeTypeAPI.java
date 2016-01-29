package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RatingsRatingCodeTypeAPI extends HollowObjectTypeAPI {

    private final RatingsRatingCodeDelegateLookupImpl delegateLookupImpl;

    RatingsRatingCodeTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "translatedTexts"
        });
        this.delegateLookupImpl = new RatingsRatingCodeDelegateLookupImpl(this);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("RatingsRatingCode", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public RatingsRatingCodeMapOfTranslatedTextsTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getRatingsRatingCodeMapOfTranslatedTextsTypeAPI();
    }

    public RatingsRatingCodeDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}