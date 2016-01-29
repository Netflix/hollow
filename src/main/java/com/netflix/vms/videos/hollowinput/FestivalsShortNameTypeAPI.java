package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class FestivalsShortNameTypeAPI extends HollowObjectTypeAPI {

    private final FestivalsShortNameDelegateLookupImpl delegateLookupImpl;

    FestivalsShortNameTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "translatedTexts"
        });
        this.delegateLookupImpl = new FestivalsShortNameDelegateLookupImpl(this);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("FestivalsShortName", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public FestivalsShortNameMapOfTranslatedTextsTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getFestivalsShortNameMapOfTranslatedTextsTypeAPI();
    }

    public FestivalsShortNameDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}