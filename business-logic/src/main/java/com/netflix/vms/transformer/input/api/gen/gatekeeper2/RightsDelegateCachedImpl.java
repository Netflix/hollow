package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class RightsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RightsDelegate {

    private final int windowsOrdinal;
    private RightsTypeAPI typeAPI;

    public RightsDelegateCachedImpl(RightsTypeAPI typeAPI, int ordinal) {
        this.windowsOrdinal = typeAPI.getWindowsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getWindowsOrdinal(int ordinal) {
        return windowsOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public RightsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RightsTypeAPI) typeAPI;
    }

}