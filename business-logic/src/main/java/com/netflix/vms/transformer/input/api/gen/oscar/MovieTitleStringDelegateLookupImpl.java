package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieTitleStringDelegateLookupImpl extends HollowObjectAbstractDelegate implements MovieTitleStringDelegate {

    private final MovieTitleStringTypeAPI typeAPI;

    public MovieTitleStringDelegateLookupImpl(MovieTitleStringTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getValue(int ordinal) {
        return typeAPI.getValue(ordinal);
    }

    public boolean isValueEqual(int ordinal, String testValue) {
        return typeAPI.isValueEqual(ordinal, testValue);
    }

    public MovieTitleStringTypeAPI getTypeAPI() {
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