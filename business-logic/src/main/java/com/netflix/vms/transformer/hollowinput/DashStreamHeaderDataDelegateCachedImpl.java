package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class DashStreamHeaderDataDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, DashStreamHeaderDataDelegate {

    private final int boxInfoOrdinal;
   private DashStreamHeaderDataTypeAPI typeAPI;

    public DashStreamHeaderDataDelegateCachedImpl(DashStreamHeaderDataTypeAPI typeAPI, int ordinal) {
        this.boxInfoOrdinal = typeAPI.getBoxInfoOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getBoxInfoOrdinal(int ordinal) {
        return boxInfoOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public DashStreamHeaderDataTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (DashStreamHeaderDataTypeAPI) typeAPI;
    }

}