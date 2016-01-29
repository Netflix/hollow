package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsTypeAPI extends HollowObjectTypeAPI {

    private final ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsDelegateLookupImpl delegateLookupImpl;

    ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "value"
        });
        this.delegateLookupImpl = new ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsDelegateLookupImpl(this);
    }

    public int getValueOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTexts", ordinal, "value");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getValueTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTranslatedTextsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}