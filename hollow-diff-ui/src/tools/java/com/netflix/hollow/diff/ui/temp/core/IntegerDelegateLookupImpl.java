package com.netflix.hollow.diff.ui.temp.core;

import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class IntegerDelegateLookupImpl extends HollowObjectAbstractDelegate implements IntegerDelegate {

    private final IntegerTypeAPI typeAPI;

    public IntegerDelegateLookupImpl(IntegerTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getValue(int ordinal) {
        return typeAPI.getValue(ordinal);
    }

    public Integer getValueBoxed(int ordinal) {
        return typeAPI.getValueBoxed(ordinal);
    }

    public IntegerTypeAPI getTypeAPI() {
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