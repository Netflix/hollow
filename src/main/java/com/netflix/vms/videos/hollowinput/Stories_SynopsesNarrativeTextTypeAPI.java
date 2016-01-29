package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class Stories_SynopsesNarrativeTextTypeAPI extends HollowObjectTypeAPI {

    private final Stories_SynopsesNarrativeTextDelegateLookupImpl delegateLookupImpl;

    Stories_SynopsesNarrativeTextTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "translatedTexts"
        });
        this.delegateLookupImpl = new Stories_SynopsesNarrativeTextDelegateLookupImpl(this);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("Stories_SynopsesNarrativeText", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public Stories_SynopsesNarrativeTextMapOfTranslatedTextsTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getStories_SynopsesNarrativeTextMapOfTranslatedTextsTypeAPI();
    }

    public Stories_SynopsesNarrativeTextDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}