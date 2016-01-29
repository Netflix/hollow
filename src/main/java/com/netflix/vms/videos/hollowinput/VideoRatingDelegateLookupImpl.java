package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoRatingDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoRatingDelegate {

    private final VideoRatingTypeAPI typeAPI;

    public VideoRatingDelegateLookupImpl(VideoRatingTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getRatingOrdinal(int ordinal) {
        return typeAPI.getRatingOrdinal(ordinal);
    }

    public long getVideoId(int ordinal) {
        return typeAPI.getVideoId(ordinal);
    }

    public Long getVideoIdBoxed(int ordinal) {
        return typeAPI.getVideoIdBoxed(ordinal);
    }

    public VideoRatingTypeAPI getTypeAPI() {
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