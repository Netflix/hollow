package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class CategoriesShortNameTypeAPI extends HollowObjectTypeAPI {

    private final CategoriesShortNameDelegateLookupImpl delegateLookupImpl;

    CategoriesShortNameTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "translatedTexts"
        });
        this.delegateLookupImpl = new CategoriesShortNameDelegateLookupImpl(this);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("CategoriesShortName", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public CategoriesShortNameMapOfTranslatedTextsTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getCategoriesShortNameMapOfTranslatedTextsTypeAPI();
    }

    public CategoriesShortNameDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}