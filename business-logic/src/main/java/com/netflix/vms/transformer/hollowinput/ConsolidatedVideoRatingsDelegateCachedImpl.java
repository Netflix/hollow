package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
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