package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsDelegateLookupImpl extends HollowObjectAbstractDelegate implements RightsDelegate {

    private final RightsTypeAPI typeAPI;

    public RightsDelegateLookupImpl(RightsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getWindowsOrdinal(int ordinal) {
        return typeAPI.getWindowsOrdinal(ordinal);
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