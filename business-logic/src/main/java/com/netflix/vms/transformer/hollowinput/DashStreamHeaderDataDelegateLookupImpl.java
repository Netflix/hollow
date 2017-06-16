package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DashStreamHeaderDataDelegateLookupImpl extends HollowObjectAbstractDelegate implements DashStreamHeaderDataDelegate {

    private final DashStreamHeaderDataTypeAPI typeAPI;

    public DashStreamHeaderDataDelegateLookupImpl(DashStreamHeaderDataTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getBoxInfoOrdinal(int ordinal) {
        return typeAPI.getBoxInfoOrdinal(ordinal);
    }

    public DashStreamHeaderDataTypeAPI getTypeAPI() {
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