package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class CertificationSystemRatingTypeAPI extends HollowObjectTypeAPI {

    private final CertificationSystemRatingDelegateLookupImpl delegateLookupImpl;

    CertificationSystemRatingTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "ratingCode",
            "ratingId",
            "maturityLevel"
        });
        this.delegateLookupImpl = new CertificationSystemRatingDelegateLookupImpl(this);
    }

    public int getRatingCodeOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("CertificationSystemRating", ordinal, "ratingCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getRatingCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getRatingId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("CertificationSystemRating", ordinal, "ratingId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getRatingIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("CertificationSystemRating", ordinal, "ratingId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getMaturityLevel(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("CertificationSystemRating", ordinal, "maturityLevel");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getMaturityLevelBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("CertificationSystemRating", ordinal, "maturityLevel");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public CertificationSystemRatingDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}