package com.netflix.hollow.diff.ui.temp.core;

import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MyEntityDelegateLookupImpl extends HollowObjectAbstractDelegate implements MyEntityDelegate {

    private final MyEntityTypeAPI typeAPI;

    public MyEntityDelegateLookupImpl(MyEntityTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getId(int ordinal) {
        ordinal = typeAPI.getIdOrdinal(ordinal);
        return ordinal == -1 ? Integer.MIN_VALUE : typeAPI.getAPI().getIntegerTypeAPI().getValue(ordinal);
    }

    public Integer getIdBoxed(int ordinal) {
        ordinal = typeAPI.getIdOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getIntegerTypeAPI().getValueBoxed(ordinal);
    }

    public int getIdOrdinal(int ordinal) {
        return typeAPI.getIdOrdinal(ordinal);
    }

    public String getName(int ordinal) {
        ordinal = typeAPI.getNameOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isNameEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getNameOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getNameOrdinal(int ordinal) {
        return typeAPI.getNameOrdinal(ordinal);
    }

    public int getProfileId(int ordinal) {
        ordinal = typeAPI.getProfileIdOrdinal(ordinal);
        return ordinal == -1 ? Integer.MIN_VALUE : typeAPI.getAPI().getProfileIdTypeAPI().getValue(ordinal);
    }

    public Integer getProfileIdBoxed(int ordinal) {
        ordinal = typeAPI.getProfileIdOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getProfileIdTypeAPI().getValueBoxed(ordinal);
    }

    public int getProfileIdOrdinal(int ordinal) {
        return typeAPI.getProfileIdOrdinal(ordinal);
    }

    public MyEntityTypeAPI getTypeAPI() {
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