package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class CategoryGroupsTypeAPI extends HollowObjectTypeAPI {

    private final CategoryGroupsDelegateLookupImpl delegateLookupImpl;

    CategoryGroupsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "categoryGroupName",
            "categoryGroupId"
        });
        this.delegateLookupImpl = new CategoryGroupsDelegateLookupImpl(this);
    }

    public int getCategoryGroupNameOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("CategoryGroups", ordinal, "categoryGroupName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public CategoryGroupsCategoryGroupNameTypeAPI getCategoryGroupNameTypeAPI() {
        return getAPI().getCategoryGroupsCategoryGroupNameTypeAPI();
    }

    public long getCategoryGroupId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("CategoryGroups", ordinal, "categoryGroupId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getCategoryGroupIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("CategoryGroups", ordinal, "categoryGroupId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public CategoryGroupsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}