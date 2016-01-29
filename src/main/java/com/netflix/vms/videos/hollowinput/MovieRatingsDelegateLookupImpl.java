package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class MovieRatingsDelegateLookupImpl extends HollowObjectAbstractDelegate implements MovieRatingsDelegate {

    private final MovieRatingsTypeAPI typeAPI;

    public MovieRatingsDelegateLookupImpl(MovieRatingsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getRatingReasonOrdinal(int ordinal) {
        return typeAPI.getRatingReasonOrdinal(ordinal);
    }

    public long getMovieId(int ordinal) {
        return typeAPI.getMovieId(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        return typeAPI.getMovieIdBoxed(ordinal);
    }

    public int getMediaOrdinal(int ordinal) {
        return typeAPI.getMediaOrdinal(ordinal);
    }

    public long getCertificationTypeId(int ordinal) {
        return typeAPI.getCertificationTypeId(ordinal);
    }

    public Long getCertificationTypeIdBoxed(int ordinal) {
        return typeAPI.getCertificationTypeIdBoxed(ordinal);
    }

    public MovieRatingsTypeAPI getTypeAPI() {
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