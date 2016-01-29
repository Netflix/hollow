package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoRatingDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoRatingDelegate {

    private final int ratingOrdinal;
    private final Long videoId;
   private VideoRatingTypeAPI typeAPI;

    public VideoRatingDelegateCachedImpl(VideoRatingTypeAPI typeAPI, int ordinal) {
        this.ratingOrdinal = typeAPI.getRatingOrdinal(ordinal);
        this.videoId = typeAPI.getVideoIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getRatingOrdinal(int ordinal) {
        return ratingOrdinal;
    }

    public long getVideoId(int ordinal) {
        return videoId.longValue();
    }

    public Long getVideoIdBoxed(int ordinal) {
        return videoId;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoRatingTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoRatingTypeAPI) typeAPI;
    }

}