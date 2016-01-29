package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class AwardsDescriptionTypeAPI extends HollowObjectTypeAPI {

    private final AwardsDescriptionDelegateLookupImpl delegateLookupImpl;

    AwardsDescriptionTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "translatedTexts"
        });
        this.delegateLookupImpl = new AwardsDescriptionDelegateLookupImpl(this);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("AwardsDescription", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public AwardsDescriptionTranslatedTextsTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getAwardsDescriptionTranslatedTextsTypeAPI();
    }

    public AwardsDescriptionDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}