package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class ConsolidatedCertificationSystemsRatingRatingCodesTypeAPI extends HollowObjectTypeAPI {

    private final ConsolidatedCertificationSystemsRatingRatingCodesDelegateLookupImpl delegateLookupImpl;

    ConsolidatedCertificationSystemsRatingRatingCodesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "translatedTexts"
        });
        this.delegateLookupImpl = new ConsolidatedCertificationSystemsRatingRatingCodesDelegateLookupImpl(this);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedCertificationSystemsRatingRatingCodes", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsTypeAPI();
    }

    public ConsolidatedCertificationSystemsRatingRatingCodesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}