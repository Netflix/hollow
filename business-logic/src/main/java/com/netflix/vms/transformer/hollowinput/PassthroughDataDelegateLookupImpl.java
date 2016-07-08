package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class PassthroughDataDelegateLookupImpl extends HollowObjectAbstractDelegate implements PassthroughDataDelegate {

    private final PassthroughDataTypeAPI typeAPI;

    public PassthroughDataDelegateLookupImpl(PassthroughDataTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getSingleValuesOrdinal(int ordinal) {
        return typeAPI.getSingleValuesOrdinal(ordinal);
    }

    public int getMultiValuesOrdinal(int ordinal) {
        return typeAPI.getMultiValuesOrdinal(ordinal);
    }

    public PassthroughDataTypeAPI getTypeAPI() {
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