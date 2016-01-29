package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoRatingTypeAPI extends HollowObjectTypeAPI {

    private final VideoRatingDelegateLookupImpl delegateLookupImpl;

    VideoRatingTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "rating",
            "videoId"
        });
        this.delegateLookupImpl = new VideoRatingDelegateLookupImpl(this);
    }

    public int getRatingOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRating", ordinal, "rating");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public VideoRatingArrayOfRatingTypeAPI getRatingTypeAPI() {
        return getAPI().getVideoRatingArrayOfRatingTypeAPI();
    }

    public long getVideoId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("VideoRating", ordinal, "videoId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getVideoIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("VideoRating", ordinal, "videoId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public VideoRatingDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}