package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoTypeTypeAPI extends HollowObjectTypeAPI {

    private final VideoTypeDelegateLookupImpl delegateLookupImpl;

    VideoTypeTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "videoId",
            "countryInfos"
        });
        this.delegateLookupImpl = new VideoTypeDelegateLookupImpl(this);
    }

    public long getVideoId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("VideoType", ordinal, "videoId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getVideoIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("VideoType", ordinal, "videoId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getCountryInfosOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoType", ordinal, "countryInfos");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public VideoTypeDescriptorListTypeAPI getCountryInfosTypeAPI() {
        return getAPI().getVideoTypeDescriptorListTypeAPI();
    }

    public VideoTypeDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}