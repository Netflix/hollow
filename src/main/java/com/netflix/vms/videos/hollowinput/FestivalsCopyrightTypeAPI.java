package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class FestivalsCopyrightTypeAPI extends HollowObjectTypeAPI {

    private final FestivalsCopyrightDelegateLookupImpl delegateLookupImpl;

    FestivalsCopyrightTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "translatedTexts"
        });
        this.delegateLookupImpl = new FestivalsCopyrightDelegateLookupImpl(this);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("FestivalsCopyright", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public FestivalsCopyrightMapOfTranslatedTextsTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getFestivalsCopyrightMapOfTranslatedTextsTypeAPI();
    }

    public FestivalsCopyrightDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}