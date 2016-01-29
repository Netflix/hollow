package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class MovieRatingsTypeAPI extends HollowObjectTypeAPI {

    private final MovieRatingsDelegateLookupImpl delegateLookupImpl;

    MovieRatingsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "ratingReason",
            "movieId",
            "media",
            "certificationTypeId"
        });
        this.delegateLookupImpl = new MovieRatingsDelegateLookupImpl(this);
    }

    public int getRatingReasonOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieRatings", ordinal, "ratingReason");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public MovieRatingsRatingReasonTypeAPI getRatingReasonTypeAPI() {
        return getAPI().getMovieRatingsRatingReasonTypeAPI();
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("MovieRatings", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("MovieRatings", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getMediaOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieRatings", ordinal, "media");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getMediaTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getCertificationTypeId(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("MovieRatings", ordinal, "certificationTypeId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getCertificationTypeIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("MovieRatings", ordinal, "certificationTypeId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public MovieRatingsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}