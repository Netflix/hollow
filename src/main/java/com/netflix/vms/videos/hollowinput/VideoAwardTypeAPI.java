package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoAwardTypeAPI extends HollowObjectTypeAPI {

    private final VideoAwardDelegateLookupImpl delegateLookupImpl;

    VideoAwardTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "award",
            "videoId"
        });
        this.delegateLookupImpl = new VideoAwardDelegateLookupImpl(this);
    }

    public int getAwardOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoAward", ordinal, "award");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public VideoAwardArrayOfAwardTypeAPI getAwardTypeAPI() {
        return getAPI().getVideoAwardArrayOfAwardTypeAPI();
    }

    public long getVideoId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("VideoAward", ordinal, "videoId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getVideoIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("VideoAward", ordinal, "videoId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public VideoAwardDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}