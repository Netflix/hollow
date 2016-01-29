package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class ConsolidatedCertificationSystemsRatingDescriptionsTypeAPI extends HollowObjectTypeAPI {

    private final ConsolidatedCertificationSystemsRatingDescriptionsDelegateLookupImpl delegateLookupImpl;

    ConsolidatedCertificationSystemsRatingDescriptionsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "translatedTexts"
        });
        this.delegateLookupImpl = new ConsolidatedCertificationSystemsRatingDescriptionsDelegateLookupImpl(this);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedCertificationSystemsRatingDescriptions", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public ConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsTypeAPI();
    }

    public ConsolidatedCertificationSystemsRatingDescriptionsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}