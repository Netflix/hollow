package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class LanguagesDelegateLookupImpl extends HollowObjectAbstractDelegate implements LanguagesDelegate {

    private final LanguagesTypeAPI typeAPI;

    public LanguagesDelegateLookupImpl(LanguagesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getLanguageId(int ordinal) {
        return typeAPI.getLanguageId(ordinal);
    }

    public Long getLanguageIdBoxed(int ordinal) {
        return typeAPI.getLanguageIdBoxed(ordinal);
    }

    public int getNameOrdinal(int ordinal) {
        return typeAPI.getNameOrdinal(ordinal);
    }

    public LanguagesTypeAPI getTypeAPI() {
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