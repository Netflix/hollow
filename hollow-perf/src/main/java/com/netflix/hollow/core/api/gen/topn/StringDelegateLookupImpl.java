package com.netflix.hollow.core.api.gen.topn;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StringDelegateLookupImpl extends HollowObjectAbstractDelegate implements StringDelegate {

    private final StringTypeAPI typeAPI;

    public StringDelegateLookupImpl(StringTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getValue(int ordinal) {
        return typeAPI.getValue(ordinal);
    }

    public boolean isValueEqual(int ordinal, String testValue) {
        return typeAPI.isValueEqual(ordinal, testValue);
    }

    public StringTypeAPI getTypeAPI() {
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