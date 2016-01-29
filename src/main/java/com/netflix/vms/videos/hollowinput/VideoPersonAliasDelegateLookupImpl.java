package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoPersonAliasDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoPersonAliasDelegate {

    private final VideoPersonAliasTypeAPI typeAPI;

    public VideoPersonAliasDelegateLookupImpl(VideoPersonAliasTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getAliasId(int ordinal) {
        return typeAPI.getAliasId(ordinal);
    }

    public Long getAliasIdBoxed(int ordinal) {
        return typeAPI.getAliasIdBoxed(ordinal);
    }

    public VideoPersonAliasTypeAPI getTypeAPI() {
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