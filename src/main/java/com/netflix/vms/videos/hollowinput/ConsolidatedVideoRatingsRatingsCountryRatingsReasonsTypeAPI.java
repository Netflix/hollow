package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTypeAPI extends HollowObjectTypeAPI {

    private final ConsolidatedVideoRatingsRatingsCountryRatingsReasonsDelegateLookupImpl delegateLookupImpl;

    ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "translatedTexts"
        });
        this.delegateLookupImpl = new ConsolidatedVideoRatingsRatingsCountryRatingsReasonsDelegateLookupImpl(this);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedVideoRatingsRatingsCountryRatingsReasons", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public ConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsTypeAPI();
    }

    public ConsolidatedVideoRatingsRatingsCountryRatingsReasonsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}