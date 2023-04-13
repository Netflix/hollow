package com.netflix.hollow.test.generated;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class AwardTypeAPI extends HollowObjectTypeAPI {

    private final AwardDelegateLookupImpl delegateLookupImpl;

    public AwardTypeAPI(AwardsAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "id",
            "winner",
            "nominees"
        });
        this.delegateLookupImpl = new AwardDelegateLookupImpl(this);
    }

    public long getId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Award", ordinal, "id");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Award", ordinal, "id");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getWinnerOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Award", ordinal, "winner");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public MovieTypeAPI getWinnerTypeAPI() {
        return getAPI().getMovieTypeAPI();
    }

    public int getNomineesOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Award", ordinal, "nominees");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public SetOfMovieTypeAPI getNomineesTypeAPI() {
        return getAPI().getSetOfMovieTypeAPI();
    }

    public AwardDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public AwardsAPI getAPI() {
        return (AwardsAPI) api;
    }

}