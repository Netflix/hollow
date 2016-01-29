package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class MovieRatingsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, MovieRatingsDelegate {

    private final int ratingReasonOrdinal;
    private final Long movieId;
    private final int mediaOrdinal;
    private final Long certificationTypeId;
   private MovieRatingsTypeAPI typeAPI;

    public MovieRatingsDelegateCachedImpl(MovieRatingsTypeAPI typeAPI, int ordinal) {
        this.ratingReasonOrdinal = typeAPI.getRatingReasonOrdinal(ordinal);
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.mediaOrdinal = typeAPI.getMediaOrdinal(ordinal);
        this.certificationTypeId = typeAPI.getCertificationTypeIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getRatingReasonOrdinal(int ordinal) {
        return ratingReasonOrdinal;
    }

    public long getMovieId(int ordinal) {
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public int getMediaOrdinal(int ordinal) {
        return mediaOrdinal;
    }

    public long getCertificationTypeId(int ordinal) {
        return certificationTypeId.longValue();
    }

    public Long getCertificationTypeIdBoxed(int ordinal) {
        return certificationTypeId;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public MovieRatingsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (MovieRatingsTypeAPI) typeAPI;
    }

}