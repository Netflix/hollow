package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoAwardDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoAwardDelegate {

    private final Long videoId;
    private final int awardOrdinal;
   private VideoAwardTypeAPI typeAPI;

    public VideoAwardDelegateCachedImpl(VideoAwardTypeAPI typeAPI, int ordinal) {
        this.videoId = typeAPI.getVideoIdBoxed(ordinal);
        this.awardOrdinal = typeAPI.getAwardOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getVideoId(int ordinal) {
        return videoId.longValue();
    }

    public Long getVideoIdBoxed(int ordinal) {
        return videoId;
    }

    public int getAwardOrdinal(int ordinal) {
        return awardOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoAwardTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoAwardTypeAPI) typeAPI;
    }

}