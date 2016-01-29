package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class ConsolidatedVideoRatingsRatingsCountryRatingsTypeAPI extends HollowObjectTypeAPI {

    private final ConsolidatedVideoRatingsRatingsCountryRatingsDelegateLookupImpl delegateLookupImpl;

    ConsolidatedVideoRatingsRatingsCountryRatingsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "advisories",
            "reasons",
            "ratingId",
            "certificationSystemId"
        });
        this.delegateLookupImpl = new ConsolidatedVideoRatingsRatingsCountryRatingsDelegateLookupImpl(this);
    }

    public int getAdvisoriesOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedVideoRatingsRatingsCountryRatings", ordinal, "advisories");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesTypeAPI getAdvisoriesTypeAPI() {
        return getAPI().getConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesTypeAPI();
    }

    public int getReasonsOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedVideoRatingsRatingsCountryRatings", ordinal, "reasons");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTypeAPI getReasonsTypeAPI() {
        return getAPI().getConsolidatedVideoRatingsRatingsCountryRatingsReasonsTypeAPI();
    }

    public long getRatingId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("ConsolidatedVideoRatingsRatingsCountryRatings", ordinal, "ratingId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getRatingIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("ConsolidatedVideoRatingsRatingsCountryRatings", ordinal, "ratingId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getCertificationSystemId(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("ConsolidatedVideoRatingsRatingsCountryRatings", ordinal, "certificationSystemId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getCertificationSystemIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("ConsolidatedVideoRatingsRatingsCountryRatings", ordinal, "certificationSystemId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public ConsolidatedVideoRatingsRatingsCountryRatingsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}