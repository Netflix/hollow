package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class CategoriesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, CategoriesDelegate {

    private final Long categoryId;
    private final int displayNameOrdinal;
    private final int shortNameOrdinal;
    private CategoriesTypeAPI typeAPI;

    public CategoriesDelegateCachedImpl(CategoriesTypeAPI typeAPI, int ordinal) {
        this.categoryId = typeAPI.getCategoryIdBoxed(ordinal);
        this.displayNameOrdinal = typeAPI.getDisplayNameOrdinal(ordinal);
        this.shortNameOrdinal = typeAPI.getShortNameOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getCategoryId(int ordinal) {
        if(categoryId == null)
            return Long.MIN_VALUE;
        return categoryId.longValue();
    }

    public Long getCategoryIdBoxed(int ordinal) {
        return categoryId;
    }

    public int getDisplayNameOrdinal(int ordinal) {
        return displayNameOrdinal;
    }

    public int getShortNameOrdinal(int ordinal) {
        return shortNameOrdinal;
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