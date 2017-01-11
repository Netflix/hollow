package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieRatingsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, MovieRatingsDelegate {

    private final Long movieId;
    private final int mediaOrdinal;
    private final Long certificationTypeId;
    private final int ratingReasonOrdinal;
   private MovieRatingsTypeAPI typeAPI;

    public MovieRatingsDelegateCachedImpl(MovieRatingsTypeAPI typeAPI, int ordinal) {
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.mediaOrdinal = typeAPI.getMediaOrdinal(ordinal);
        this.certificationTypeId = typeAPI.getCertificationTypeIdBoxed(ordinal);
        this.ratingReasonOrdinal = typeAPI.getRatingReasonOrdinal(ordinal);
        this.typeAPI = typeAPI;
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

    public int getRatingReasonOrdinal(int ordinal) {
        return ratingReasonOrdinal;
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