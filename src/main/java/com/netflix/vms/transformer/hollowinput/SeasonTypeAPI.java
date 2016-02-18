package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class SeasonTypeAPI extends HollowObjectTypeAPI {

    private final SeasonDelegateLookupImpl delegateLookupImpl;

    SeasonTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "sequenceNumber",
            "children",
            "movieId"
        });
        this.delegateLookupImpl = new SeasonDelegateLookupImpl(this);
    }

    public long getSequenceNumber(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Season", ordinal, "sequenceNumber");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Season", ordinal, "sequenceNumber");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getChildrenOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Season", ordinal, "children");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public EpisodeListTypeAPI getChildrenTypeAPI() {
        return getAPI().getEpisodeListTypeAPI();
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("Season", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("Season", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public SeasonDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}