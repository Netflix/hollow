package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoRatingRatingReasonDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoRatingRatingReasonDelegate {

    private final Boolean ordered;
    private final Boolean imageOnly;
    private final int idsOrdinal;
   private VideoRatingRatingReasonTypeAPI typeAPI;

    public VideoRatingRatingReasonDelegateCachedImpl(VideoRatingRatingReasonTypeAPI typeAPI, int ordinal) {
        this.ordered = typeAPI.getOrderedBoxed(ordinal);
        this.imageOnly = typeAPI.getImageOnlyBoxed(ordinal);
        this.idsOrdinal = typeAPI.getIdsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public boolean getOrdered(int ordinal) {
        return ordered.booleanValue();
    }

    public Boolean getOrderedBoxed(int ordinal) {
        return ordered;
    }

    public boolean getImageOnly(int ordinal) {
        return imageOnly.booleanValue();
    }

    public Boolean getImageOnlyBoxed(int ordinal) {
        return imageOnly;
    }

    public int getIdsOrdinal(int ordinal) {
        return idsOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoRatingRatingReasonTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoRatingRatingReasonTypeAPI) typeAPI;
    }

}