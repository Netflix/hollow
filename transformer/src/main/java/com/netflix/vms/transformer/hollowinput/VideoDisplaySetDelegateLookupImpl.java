package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoDisplaySetDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoDisplaySetDelegate {

    private final VideoDisplaySetTypeAPI typeAPI;

    public VideoDisplaySetDelegateLookupImpl(VideoDisplaySetTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getTopNodeId(int ordinal) {
        return typeAPI.getTopNodeId(ordinal);
    }

    public Long getTopNodeIdBoxed(int ordinal) {
        return typeAPI.getTopNodeIdBoxed(ordinal);
    }

    public int getSetsOrdinal(int ordinal) {
        return typeAPI.getSetsOrdinal(ordinal);
    }

    public VideoDisplaySetTypeAPI getTypeAPI() {
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