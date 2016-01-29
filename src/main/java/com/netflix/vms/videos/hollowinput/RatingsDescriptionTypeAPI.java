package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RatingsDescriptionTypeAPI extends HollowObjectTypeAPI {

    private final RatingsDescriptionDelegateLookupImpl delegateLookupImpl;

    RatingsDescriptionTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "translatedTexts"
        });
        this.delegateLookupImpl = new RatingsDescriptionDelegateLookupImpl(this);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("RatingsDescription", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public RatingsDescriptionMapOfTranslatedTextsTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getRatingsDescriptionMapOfTranslatedTextsTypeAPI();
    }

    public RatingsDescriptionDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}