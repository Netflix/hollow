package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class ConsolidatedCertificationSystemsRatingTypeAPI extends HollowObjectTypeAPI {

    private final ConsolidatedCertificationSystemsRatingDelegateLookupImpl delegateLookupImpl;

    ConsolidatedCertificationSystemsRatingTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "ratingCode",
            "ratingCodes",
            "ratingId",
            "maturityLevel",
            "descriptions"
        });
        this.delegateLookupImpl = new ConsolidatedCertificationSystemsRatingDelegateLookupImpl(this);
    }

    public int getRatingCodeOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedCertificationSystemsRating", ordinal, "ratingCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getRatingCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getRatingCodesOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedCertificationSystemsRating", ordinal, "ratingCodes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public ConsolidatedCertificationSystemsRatingRatingCodesTypeAPI getRatingCodesTypeAPI() {
        return getAPI().getConsolidatedCertificationSystemsRatingRatingCodesTypeAPI();
    }

    public long getRatingId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("ConsolidatedCertificationSystemsRating", ordinal, "ratingId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getRatingIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("ConsolidatedCertificationSystemsRating", ordinal, "ratingId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getMaturityLevel(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("ConsolidatedCertificationSystemsRating", ordinal, "maturityLevel");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getMaturityLevelBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("ConsolidatedCertificationSystemsRating", ordinal, "maturityLevel");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getDescriptionsOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedCertificationSystemsRating", ordinal, "descriptions");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public ConsolidatedCertificationSystemsRatingDescriptionsTypeAPI getDescriptionsTypeAPI() {
        return getAPI().getConsolidatedCertificationSystemsRatingDescriptionsTypeAPI();
    }

    public ConsolidatedCertificationSystemsRatingDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}