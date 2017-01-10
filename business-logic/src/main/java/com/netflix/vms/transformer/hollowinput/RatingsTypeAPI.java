package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class RatingsTypeAPI extends HollowObjectTypeAPI {

    private final RatingsDelegateLookupImpl delegateLookupImpl;

    RatingsTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "ratingId",
            "ratingCode",
            "description"
        });
        this.delegateLookupImpl = new RatingsDelegateLookupImpl(this);
    }

    public long getRatingId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Ratings", ordinal, "ratingId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getRatingIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Ratings", ordinal, "ratingId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getRatingCodeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Ratings", ordinal, "ratingCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public TranslatedTextTypeAPI getRatingCodeTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getDescriptionOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Ratings", ordinal, "description");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public TranslatedTextTypeAPI getDescriptionTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public RatingsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}