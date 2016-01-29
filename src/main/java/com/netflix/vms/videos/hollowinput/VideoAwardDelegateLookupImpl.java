package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoAwardDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoAwardDelegate {

    private final VideoAwardTypeAPI typeAPI;

    public VideoAwardDelegateLookupImpl(VideoAwardTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getAwardOrdinal(int ordinal) {
        return typeAPI.getAwardOrdinal(ordinal);
    }

    public long getVideoId(int ordinal) {
        return typeAPI.getVideoId(ordinal);
    }

    public Long getVideoIdBoxed(int ordinal) {
        return typeAPI.getVideoIdBoxed(ordinal);
    }

    public VideoAwardTypeAPI getTypeAPI() {
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