package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoRightsTypeAPI extends HollowObjectTypeAPI {

    private final VideoRightsDelegateLookupImpl delegateLookupImpl;

    VideoRightsTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieId",
            "countryCode",
            "rights",
            "flags"
        });
        this.delegateLookupImpl = new VideoRightsDelegateLookupImpl(this);
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("VideoRights", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("VideoRights", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getCountryCodeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRights", ordinal, "countryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getCountryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getRightsOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRights", ordinal, "rights");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public VideoRightsRightsTypeAPI getRightsTypeAPI() {
        return getAPI().getVideoRightsRightsTypeAPI();
    }

    public int getFlagsOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRights", ordinal, "flags");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public VideoRightsFlagsTypeAPI getFlagsTypeAPI() {
        return getAPI().getVideoRightsFlagsTypeAPI();
    }

    public VideoRightsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}