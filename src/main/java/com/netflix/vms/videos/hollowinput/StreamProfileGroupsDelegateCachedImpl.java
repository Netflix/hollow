package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class StreamProfileGroupsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, StreamProfileGroupsDelegate {

    private final int groupNameOrdinal;
    private final int streamProfileIdsOrdinal;
   private StreamProfileGroupsTypeAPI typeAPI;

    public StreamProfileGroupsDelegateCachedImpl(StreamProfileGroupsTypeAPI typeAPI, int ordinal) {
        this.groupNameOrdinal = typeAPI.getGroupNameOrdinal(ordinal);
        this.streamProfileIdsOrdinal = typeAPI.getStreamProfileIdsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getGroupNameOrdinal(int ordinal) {
        return groupNameOrdinal;
    }

    public int getStreamProfileIdsOrdinal(int ordinal) {
        return streamProfileIdsOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public StreamProfileGroupsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (StreamProfileGroupsTypeAPI) typeAPI;
    }

}