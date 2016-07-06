package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoRatingRatingTypeAPI extends HollowObjectTypeAPI {

    private final VideoRatingRatingDelegateLookupImpl delegateLookupImpl;

    VideoRatingRatingTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "reason",
            "ratingId",
            "certificationSystemId"
        });
        this.delegateLookupImpl = new VideoRatingRatingDelegateLookupImpl(this);
    }

    public int getReasonOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRatingRating", ordinal, "reason");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public VideoRatingRatingReasonTypeAPI getReasonTypeAPI() {
        return getAPI().getVideoRatingRatingReasonTypeAPI();
    }

    public long getRatingId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("VideoRatingRating", ordinal, "ratingId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getRatingIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("VideoRatingRating", ordinal, "ratingId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getCertificationSystemId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("VideoRatingRating", ordinal, "certificationSystemId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getCertificationSystemIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("VideoRatingRating", ordinal, "certificationSystemId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public VideoRatingRatingDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}