package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class ConsolidatedVideoRatingsTypeAPI extends HollowObjectTypeAPI {

    private final ConsolidatedVideoRatingsDelegateLookupImpl delegateLookupImpl;

    ConsolidatedVideoRatingsTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "ratings",
            "videoId"
        });
        this.delegateLookupImpl = new ConsolidatedVideoRatingsDelegateLookupImpl(this);
    }

    public int getRatingsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedVideoRatings", ordinal, "ratings");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public ConsolidatedVideoRatingListTypeAPI getRatingsTypeAPI() {
        return getAPI().getConsolidatedVideoRatingListTypeAPI();
    }

    public long getVideoId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("ConsolidatedVideoRatings", ordinal, "videoId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getVideoIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("ConsolidatedVideoRatings", ordinal, "videoId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public ConsolidatedVideoRatingsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}