package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DrmInfoStringDelegateLookupImpl extends HollowObjectAbstractDelegate implements DrmInfoStringDelegate {

    private final DrmInfoStringTypeAPI typeAPI;

    public DrmInfoStringDelegateLookupImpl(DrmInfoStringTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getValue(int ordinal) {
        return typeAPI.getValue(ordinal);
    }

    public boolean isValueEqual(int ordinal, String testValue) {
        return typeAPI.isValueEqual(ordinal, testValue);
    }

    public DrmInfoStringTypeAPI getTypeAPI() {
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