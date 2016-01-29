package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class FestivalsDescriptionTypeAPI extends HollowObjectTypeAPI {

    private final FestivalsDescriptionDelegateLookupImpl delegateLookupImpl;

    FestivalsDescriptionTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "translatedTexts"
        });
        this.delegateLookupImpl = new FestivalsDescriptionDelegateLookupImpl(this);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("FestivalsDescription", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public FestivalsDescriptionMapOfTranslatedTextsTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getFestivalsDescriptionMapOfTranslatedTextsTypeAPI();
    }

    public FestivalsDescriptionDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}