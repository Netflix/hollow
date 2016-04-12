package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoDisplaySetDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoDisplaySetDelegate {

    private final Long topNodeId;
    private final int setsOrdinal;
   private VideoDisplaySetTypeAPI typeAPI;

    public VideoDisplaySetDelegateCachedImpl(VideoDisplaySetTypeAPI typeAPI, int ordinal) {
        this.topNodeId = typeAPI.getTopNodeIdBoxed(ordinal);
        this.setsOrdinal = typeAPI.getSetsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getTopNodeId(int ordinal) {
        return topNodeId.longValue();
    }

    public Long getTopNodeIdBoxed(int ordinal) {
        return topNodeId;
    }

    public int getSetsOrdinal(int ordinal) {
        return setsOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoDisplaySetTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoDisplaySetTypeAPI) typeAPI;
    }

}