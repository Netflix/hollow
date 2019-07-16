package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DistributorNameDelegateLookupImpl extends HollowObjectAbstractDelegate implements DistributorNameDelegate {

    private final DistributorNameTypeAPI typeAPI;

    public DistributorNameDelegateLookupImpl(DistributorNameTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getValue(int ordinal) {
        return typeAPI.getValue(ordinal);
    }

    public boolean isValueEqual(int ordinal, String testValue) {
        return typeAPI.isValueEqual(ordinal, testValue);
    }

    public DistributorNameTypeAPI getTypeAPI() {
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