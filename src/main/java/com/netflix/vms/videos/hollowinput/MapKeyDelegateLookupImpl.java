package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class MapKeyDelegateLookupImpl extends HollowObjectAbstractDelegate implements MapKeyDelegate {

    private final MapKeyTypeAPI typeAPI;

    public MapKeyDelegateLookupImpl(MapKeyTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getKey(int ordinal) {
        return typeAPI.getKey(ordinal);
    }

    public boolean isKeyEqual(int ordinal, String testValue) {
        return typeAPI.isKeyEqual(ordinal, testValue);
    }

    public MapKeyTypeAPI getTypeAPI() {
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