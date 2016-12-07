package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
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