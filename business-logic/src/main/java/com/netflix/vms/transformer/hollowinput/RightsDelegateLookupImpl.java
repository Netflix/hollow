package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsDelegateLookupImpl extends HollowObjectAbstractDelegate implements RightsDelegate {

    private final RightsTypeAPI typeAPI;

    public RightsDelegateLookupImpl(RightsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getWindowsOrdinal(int ordinal) {
        return typeAPI.getWindowsOrdinal(ordinal);
    }

    public int getContractsOrdinal(int ordinal) {
        return typeAPI.getContractsOrdinal(ordinal);
    }

    public RightsTypeAPI getTypeAPI() {
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