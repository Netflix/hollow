package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoRightsRightsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoRightsRightsDelegate {

    private final int windowsOrdinal;
    private final int contractsOrdinal;
   private VideoRightsRightsTypeAPI typeAPI;

    public VideoRightsRightsDelegateCachedImpl(VideoRightsRightsTypeAPI typeAPI, int ordinal) {
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

    public VideoRightsRightsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoRightsRightsTypeAPI) typeAPI;
    }

}