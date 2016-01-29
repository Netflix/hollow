package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class CategoryGroupsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, CategoryGroupsDelegate {

    private final int categoryGroupNameOrdinal;
    private final Long categoryGroupId;
   private CategoryGroupsTypeAPI typeAPI;

    public CategoryGroupsDelegateCachedImpl(CategoryGroupsTypeAPI typeAPI, int ordinal) {
        this.categoryGroupNameOrdinal = typeAPI.getCategoryGroupNameOrdinal(ordinal);
        this.categoryGroupId = typeAPI.getCategoryGroupIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getCategoryGroupNameOrdinal(int ordinal) {
        return categoryGroupNameOrdinal;
    }

    public long getCategoryGroupId(int ordinal) {
        return categoryGroupId.longValue();
    }

    public Long getCategoryGroupIdBoxed(int ordinal) {
        return categoryGroupId;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public CategoryGroupsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (CategoryGroupsTypeAPI) typeAPI;
    }

}