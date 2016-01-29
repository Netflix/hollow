package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class CategoriesDisplayNameTypeAPI extends HollowObjectTypeAPI {

    private final CategoriesDisplayNameDelegateLookupImpl delegateLookupImpl;

    CategoriesDisplayNameTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "translatedTexts"
        });
        this.delegateLookupImpl = new CategoriesDisplayNameDelegateLookupImpl(this);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("CategoriesDisplayName", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public CategoriesDisplayNameMapOfTranslatedTextsTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getCategoriesDisplayNameMapOfTranslatedTextsTypeAPI();
    }

    public CategoriesDisplayNameDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}