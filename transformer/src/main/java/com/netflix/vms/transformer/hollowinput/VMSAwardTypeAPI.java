package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VMSAwardTypeAPI extends HollowObjectTypeAPI {

    private final VMSAwardDelegateLookupImpl delegateLookupImpl;

    VMSAwardTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "awardId",
            "sequenceNumber",
            "festivalId",
            "countryCode",
            "isMovieAward"
        });
        this.delegateLookupImpl = new VMSAwardDelegateLookupImpl(this);
    }

    public long getAwardId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("VMSAward", ordinal, "awardId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getAwardIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("VMSAward", ordinal, "awardId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getSequenceNumber(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("VMSAward", ordinal, "sequenceNumber");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("VMSAward", ordinal, "sequenceNumber");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getFestivalId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("VMSAward", ordinal, "festivalId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getFestivalIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("VMSAward", ordinal, "festivalId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getCountryCodeOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("VMSAward", ordinal, "countryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StringTypeAPI getCountryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public boolean getIsMovieAward(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleBoolean("VMSAward", ordinal, "isMovieAward") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]) == Boolean.TRUE;
    }

    public Boolean getIsMovieAwardBoxed(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleBoolean("VMSAward", ordinal, "isMovieAward");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]);
    }



    public VMSAwardDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}