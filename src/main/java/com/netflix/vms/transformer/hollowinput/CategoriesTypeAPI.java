package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class CategoriesTypeAPI extends HollowObjectTypeAPI {

    private final CategoriesDelegateLookupImpl delegateLookupImpl;

    CategoriesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "categoryId",
            "displayName",
            "shortName"
        });
        this.delegateLookupImpl = new CategoriesDelegateLookupImpl(this);
    }

    public long getCategoryId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Categories", ordinal, "categoryId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getCategoryIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Categories", ordinal, "categoryId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getDisplayNameOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Categories", ordinal, "displayName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public TranslatedTextTypeAPI getDisplayNameTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getShortNameOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Categories", ordinal, "shortName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public TranslatedTextTypeAPI getShortNameTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public CategoriesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}