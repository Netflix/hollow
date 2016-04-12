package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoTypeTypeAPI extends HollowObjectTypeAPI {

    private final VideoTypeDelegateLookupImpl delegateLookupImpl;

    VideoTypeTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "videoId",
            "isTV",
            "type"
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



    public boolean getIsTV(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("VideoType", ordinal, "isTV") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]) == Boolean.TRUE;
    }

    public Boolean getIsTVBoxed(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("VideoType", ordinal, "isTV");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]);
    }



    public int getTypeOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoType", ordinal, "type");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public VideoTypeDescriptorListTypeAPI getTypeTypeAPI() {
        return getAPI().getVideoTypeDescriptorListTypeAPI();
    }

    public VideoTypeDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}