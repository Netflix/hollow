package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoPersonAliasDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoPersonAliasDelegate {

    private final Long aliasId;
   private VideoPersonAliasTypeAPI typeAPI;

    public VideoPersonAliasDelegateCachedImpl(VideoPersonAliasTypeAPI typeAPI, int ordinal) {
        this.aliasId = typeAPI.getAliasIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getAliasId(int ordinal) {
        return aliasId.longValue();
    }

    public Long getAliasIdBoxed(int ordinal) {
        return aliasId;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoPersonAliasTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoPersonAliasTypeAPI) typeAPI;
    }

}