package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class VideoRatingAdvisoriesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoRatingAdvisoriesDelegate {

    private final Boolean ordered;
    private final Boolean imageOnly;
    private final int idsOrdinal;
   private VideoRatingAdvisoriesTypeAPI typeAPI;

    public VideoRatingAdvisoriesDelegateCachedImpl(VideoRatingAdvisoriesTypeAPI typeAPI, int ordinal) {
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

    public VideoRatingAdvisoriesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoRatingAdvisoriesTypeAPI) typeAPI;
    }

}