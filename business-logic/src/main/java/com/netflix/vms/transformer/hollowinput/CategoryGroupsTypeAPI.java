package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class CategoryGroupsTypeAPI extends HollowObjectTypeAPI {

    private final CategoryGroupsDelegateLookupImpl delegateLookupImpl;

    CategoryGroupsTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "categoryGroupId",
            "categoryGroupName"
        });
        this.delegateLookupImpl = new CategoryGroupsDelegateLookupImpl(this);
    }

    public long getCategoryGroupId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("CategoryGroups", ordinal, "categoryGroupId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getCategoryGroupIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("CategoryGroups", ordinal, "categoryGroupId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getCategoryGroupNameOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("CategoryGroups", ordinal, "categoryGroupName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public TranslatedTextTypeAPI getCategoryGroupNameTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public CategoryGroupsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}