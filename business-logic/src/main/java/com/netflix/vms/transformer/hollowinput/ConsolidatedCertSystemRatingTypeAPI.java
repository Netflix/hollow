package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class ConsolidatedCertSystemRatingTypeAPI extends HollowObjectTypeAPI {

    private final ConsolidatedCertSystemRatingDelegateLookupImpl delegateLookupImpl;

    ConsolidatedCertSystemRatingTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "ratingId",
            "maturityLevel",
            "ratingCode",
            "ratingCodes",
            "descriptions"
        });
        this.delegateLookupImpl = new ConsolidatedCertSystemRatingDelegateLookupImpl(this);
    }

    public long getRatingId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("ConsolidatedCertSystemRating", ordinal, "ratingId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getRatingIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("ConsolidatedCertSystemRating", ordinal, "ratingId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getMaturityLevel(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("ConsolidatedCertSystemRating", ordinal, "maturityLevel");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getMaturityLevelBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("ConsolidatedCertSystemRating", ordinal, "maturityLevel");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getRatingCodeOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedCertSystemRating", ordinal, "ratingCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getRatingCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getRatingCodesOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedCertSystemRating", ordinal, "ratingCodes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public TranslatedTextTypeAPI getRatingCodesTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getDescriptionsOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedCertSystemRating", ordinal, "descriptions");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public TranslatedTextTypeAPI getDescriptionsTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public ConsolidatedCertSystemRatingDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}