package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class CategoriesTypeAPI extends HollowObjectTypeAPI {

    private final CategoriesDelegateLookupImpl delegateLookupImpl;

    CategoriesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "displayName",
            "shortName",
            "categoryId"
        });
        this.delegateLookupImpl = new CategoriesDelegateLookupImpl(this);
    }

    public int getDisplayNameOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("Categories", ordinal, "displayName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public CategoriesDisplayNameTypeAPI getDisplayNameTypeAPI() {
        return getAPI().getCategoriesDisplayNameTypeAPI();
    }

    public int getShortNameOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Categories", ordinal, "shortName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public CategoriesShortNameTypeAPI getShortNameTypeAPI() {
        return getAPI().getCategoriesShortNameTypeAPI();
    }

    public long getCategoryId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("Categories", ordinal, "categoryId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getCategoryIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("Categories", ordinal, "categoryId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public CategoriesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}