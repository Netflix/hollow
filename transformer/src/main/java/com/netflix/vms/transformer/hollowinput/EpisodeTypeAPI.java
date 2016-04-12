package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class EpisodeTypeAPI extends HollowObjectTypeAPI {

    private final EpisodeDelegateLookupImpl delegateLookupImpl;

    EpisodeTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "parentSequenceNumber",
            "sequenceNumber",
            "movieId",
            "altId"
        });
        this.delegateLookupImpl = new EpisodeDelegateLookupImpl(this);
    }

    public long getParentSequenceNumber(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Episode", ordinal, "parentSequenceNumber");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getParentSequenceNumberBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Episode", ordinal, "parentSequenceNumber");
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
            return missingDataHandler().handleLong("Episode", ordinal, "sequenceNumber");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("Episode", ordinal, "sequenceNumber");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getMovieId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("Episode", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("Episode", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getAltId(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("Episode", ordinal, "altId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getAltIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("Episode", ordinal, "altId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public EpisodeDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}