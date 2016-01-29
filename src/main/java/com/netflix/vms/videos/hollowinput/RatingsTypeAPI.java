package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RatingsTypeAPI extends HollowObjectTypeAPI {

    private final RatingsDelegateLookupImpl delegateLookupImpl;

    RatingsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "ratingCode",
            "ratingId",
            "description"
        });
        this.delegateLookupImpl = new RatingsDelegateLookupImpl(this);
    }

    public int getRatingCodeOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("Ratings", ordinal, "ratingCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public RatingsRatingCodeTypeAPI getRatingCodeTypeAPI() {
        return getAPI().getRatingsRatingCodeTypeAPI();
    }

    public long getRatingId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("Ratings", ordinal, "ratingId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getRatingIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("Ratings", ordinal, "ratingId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getDescriptionOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Ratings", ordinal, "description");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public RatingsDescriptionTypeAPI getDescriptionTypeAPI() {
        return getAPI().getRatingsDescriptionTypeAPI();
    }

    public RatingsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}