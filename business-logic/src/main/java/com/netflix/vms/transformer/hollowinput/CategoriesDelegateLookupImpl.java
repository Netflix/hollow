package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class CategoriesDelegateLookupImpl extends HollowObjectAbstractDelegate implements CategoriesDelegate {

    private final CategoriesTypeAPI typeAPI;

    public CategoriesDelegateLookupImpl(CategoriesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getCategoryId(int ordinal) {
        return typeAPI.getCategoryId(ordinal);
    }

    public Long getCategoryIdBoxed(int ordinal) {
        return typeAPI.getCategoryIdBoxed(ordinal);
    }

    public int getDisplayNameOrdinal(int ordinal) {
        return typeAPI.getDisplayNameOrdinal(ordinal);
    }

    public int getShortNameOrdinal(int ordinal) {
        return typeAPI.getShortNameOrdinal(ordinal);
    }

    public CategoriesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}