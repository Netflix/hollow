package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

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