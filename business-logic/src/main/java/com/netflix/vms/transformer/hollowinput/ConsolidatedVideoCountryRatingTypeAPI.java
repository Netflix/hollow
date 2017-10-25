package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class ConsolidatedVideoCountryRatingTypeAPI extends HollowObjectTypeAPI {

    private final ConsolidatedVideoCountryRatingDelegateLookupImpl delegateLookupImpl;

    public ConsolidatedVideoCountryRatingTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "advisories",
            "reasons",
            "ratingId",
            "certificationSystemId"
        });
        this.delegateLookupImpl = new ConsolidatedVideoCountryRatingDelegateLookupImpl(this);
    }

    public int getAdvisoriesOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedVideoCountryRating", ordinal, "advisories");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public VideoRatingAdvisoriesTypeAPI getAdvisoriesTypeAPI() {
        return getAPI().getVideoRatingAdvisoriesTypeAPI();
    }

    public int getReasonsOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedVideoCountryRating", ordinal, "reasons");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public TranslatedTextTypeAPI getReasonsTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public long getRatingId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("ConsolidatedVideoCountryRating", ordinal, "ratingId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getRatingIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("ConsolidatedVideoCountryRating", ordinal, "ratingId");
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
            return missingDataHandler().handleLong("ConsolidatedVideoCountryRating", ordinal, "certificationSystemId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getCertificationSystemIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("ConsolidatedVideoCountryRating", ordinal, "certificationSystemId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public ConsolidatedVideoCountryRatingDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}