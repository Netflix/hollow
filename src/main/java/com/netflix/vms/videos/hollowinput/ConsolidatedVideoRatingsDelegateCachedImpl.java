package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class ConsolidatedVideoRatingsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ConsolidatedVideoRatingsDelegate {

    private final int ratingsOrdinal;
    private final Long videoId;
   private ConsolidatedVideoRatingsTypeAPI typeAPI;

    public ConsolidatedVideoRatingsDelegateCachedImpl(ConsolidatedVideoRatingsTypeAPI typeAPI, int ordinal) {
        this.ratingsOrdinal = typeAPI.getRatingsOrdinal(ordinal);
        this.videoId = typeAPI.getVideoIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getRatingsOrdinal(int ordinal) {
        return ratingsOrdinal;
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

    public ConsolidatedVideoRatingsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ConsolidatedVideoRatingsTypeAPI) typeAPI;
    }

}