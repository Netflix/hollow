package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class CategoriesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, CategoriesDelegate {

    private final int displayNameOrdinal;
    private final int shortNameOrdinal;
    private final Long categoryId;
   private CategoriesTypeAPI typeAPI;

    public CategoriesDelegateCachedImpl(CategoriesTypeAPI typeAPI, int ordinal) {
        this.displayNameOrdinal = typeAPI.getDisplayNameOrdinal(ordinal);
        this.shortNameOrdinal = typeAPI.getShortNameOrdinal(ordinal);
        this.categoryId = typeAPI.getCategoryIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getDisplayNameOrdinal(int ordinal) {
        return displayNameOrdinal;
    }

    public int getShortNameOrdinal(int ordinal) {
        return shortNameOrdinal;
    }

    public long getCategoryId(int ordinal) {
        return categoryId.longValue();
    }

    public Long getCategoryIdBoxed(int ordinal) {
        return categoryId;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public CategoriesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (CategoriesTypeAPI) typeAPI;
    }

}