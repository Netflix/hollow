package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class VideoRatingRatingDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoRatingRatingDelegate {

    private final int reasonOrdinal;
    private final Long ratingId;
    private final Long certificationSystemId;
   private VideoRatingRatingTypeAPI typeAPI;

    public VideoRatingRatingDelegateCachedImpl(VideoRatingRatingTypeAPI typeAPI, int ordinal) {
        this.reasonOrdinal = typeAPI.getReasonOrdinal(ordinal);
        this.ratingId = typeAPI.getRatingIdBoxed(ordinal);
        this.certificationSystemId = typeAPI.getCertificationSystemIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getReasonOrdinal(int ordinal) {
        return reasonOrdinal;
    }

    public long getRatingId(int ordinal) {
        return ratingId.longValue();
    }

    public Long getRatingIdBoxed(int ordinal) {
        return ratingId;
    }

    public long getCertificationSystemId(int ordinal) {
        return certificationSystemId.longValue();
    }

    public Long getCertificationSystemIdBoxed(int ordinal) {
        return certificationSystemId;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoRatingRatingTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoRatingRatingTypeAPI) typeAPI;
    }

}