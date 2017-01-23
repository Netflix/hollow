package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class PassthroughDataDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, PassthroughDataDelegate {

    private final int singleValuesOrdinal;
    private final int multiValuesOrdinal;
   private PassthroughDataTypeAPI typeAPI;

    public PassthroughDataDelegateCachedImpl(PassthroughDataTypeAPI typeAPI, int ordinal) {
        this.singleValuesOrdinal = typeAPI.getSingleValuesOrdinal(ordinal);
        this.multiValuesOrdinal = typeAPI.getMultiValuesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getSingleValuesOrdinal(int ordinal) {
        return singleValuesOrdinal;
    }

    public int getMultiValuesOrdinal(int ordinal) {
        return multiValuesOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public PassthroughDataTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (PassthroughDataTypeAPI) typeAPI;
    }

}