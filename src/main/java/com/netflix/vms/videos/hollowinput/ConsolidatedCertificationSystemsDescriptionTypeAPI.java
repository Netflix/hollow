package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class ConsolidatedCertificationSystemsDescriptionTypeAPI extends HollowObjectTypeAPI {

    private final ConsolidatedCertificationSystemsDescriptionDelegateLookupImpl delegateLookupImpl;

    ConsolidatedCertificationSystemsDescriptionTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "translatedTexts"
        });
        this.delegateLookupImpl = new ConsolidatedCertificationSystemsDescriptionDelegateLookupImpl(this);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedCertificationSystemsDescription", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public ConsolidatedCertificationSystemsDescriptionMapOfTranslatedTextsTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getConsolidatedCertificationSystemsDescriptionMapOfTranslatedTextsTypeAPI();
    }

    public ConsolidatedCertificationSystemsDescriptionDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}