package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class MapKeyDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, MapKeyDelegate {

    private final String key;
   private MapKeyTypeAPI typeAPI;

    public MapKeyDelegateCachedImpl(MapKeyTypeAPI typeAPI, int ordinal) {
        this.key = typeAPI.getKey(ordinal);
        this.typeAPI = typeAPI;
    }

    public String getKey(int ordinal) {
        return key;
    }

    public boolean isKeyEqual(int ordinal, String testValue) {
        if(testValue == null)
            return key == null;
        return testValue.equals(key);
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public MapKeyTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (MapKeyTypeAPI) typeAPI;
    }

}