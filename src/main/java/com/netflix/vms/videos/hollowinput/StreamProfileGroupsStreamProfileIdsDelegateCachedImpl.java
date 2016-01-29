package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class StreamProfileGroupsStreamProfileIdsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, StreamProfileGroupsStreamProfileIdsDelegate {

    private final Long value;
   private StreamProfileGroupsStreamProfileIdsTypeAPI typeAPI;

    public StreamProfileGroupsStreamProfileIdsDelegateCachedImpl(StreamProfileGroupsStreamProfileIdsTypeAPI typeAPI, int ordinal) {
        this.value = typeAPI.getValueBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getValue(int ordinal) {
        return value.longValue();
    }

    public Long getValueBoxed(int ordinal) {
        return value;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public StreamProfileGroupsStreamProfileIdsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (StreamProfileGroupsStreamProfileIdsTypeAPI) typeAPI;
    }

}