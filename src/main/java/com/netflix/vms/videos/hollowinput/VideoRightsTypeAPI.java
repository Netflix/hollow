package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoRightsTypeAPI extends HollowObjectTypeAPI {

    private final VideoRightsDelegateLookupImpl delegateLookupImpl;

    VideoRightsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "countryCode",
            "rights",
            "flags",
            "movieId"
        });
        this.delegateLookupImpl = new VideoRightsDelegateLookupImpl(this);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRights", ordinal, "countryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getCountryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getRightsOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRights", ordinal, "rights");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public VideoRightsRightsTypeAPI getRightsTypeAPI() {
        return getAPI().getVideoRightsRightsTypeAPI();
    }

    public int getFlagsOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRights", ordinal, "flags");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public VideoRightsFlagsTypeAPI getFlagsTypeAPI() {
        return getAPI().getVideoRightsFlagsTypeAPI();
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("VideoRights", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("VideoRights", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public VideoRightsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}