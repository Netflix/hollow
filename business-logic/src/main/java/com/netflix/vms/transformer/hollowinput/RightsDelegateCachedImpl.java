package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class RightsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RightsDelegate {

    private final int windowsOrdinal;
    private final int contractsOrdinal;
   private RightsTypeAPI typeAPI;

    public RightsDelegateCachedImpl(RightsTypeAPI typeAPI, int ordinal) {
        this.windowsOrdinal = typeAPI.getWindowsOrdinal(ordinal);
        this.contractsOrdinal = typeAPI.getContractsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getWindowsOrdinal(int ordinal) {
        return windowsOrdinal;
    }

    public int getContractsOrdinal(int ordinal) {
        return contractsOrdinal;
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