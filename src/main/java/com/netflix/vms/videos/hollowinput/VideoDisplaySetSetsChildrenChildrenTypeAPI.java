package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoDisplaySetSetsChildrenChildrenTypeAPI extends HollowObjectTypeAPI {

    private final VideoDisplaySetSetsChildrenChildrenDelegateLookupImpl delegateLookupImpl;

    VideoDisplaySetSetsChildrenChildrenTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "parentSequenceNumber",
            "sequenceNumber",
            "movieId",
            "altId"
        });
        this.delegateLookupImpl = new VideoDisplaySetSetsChildrenChildrenDelegateLookupImpl(this);
    }

    public long getParentSequenceNumber(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("VideoDisplaySetSetsChildrenChildren", ordinal, "parentSequenceNumber");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getParentSequenceNumberBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("VideoDisplaySetSetsChildrenChildren", ordinal, "parentSequenceNumber");
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
            return missingDataHandler().handleLong("VideoDisplaySetSetsChildrenChildren", ordinal, "sequenceNumber");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("VideoDisplaySetSetsChildrenChildren", ordinal, "sequenceNumber");
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
            return missingDataHandler().handleLong("VideoDisplaySetSetsChildrenChildren", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("VideoDisplaySetSetsChildrenChildren", ordinal, "movieId");
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
            return missingDataHandler().handleLong("VideoDisplaySetSetsChildrenChildren", ordinal, "altId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getAltIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("VideoDisplaySetSetsChildrenChildren", ordinal, "altId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public VideoDisplaySetSetsChildrenChildrenDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}