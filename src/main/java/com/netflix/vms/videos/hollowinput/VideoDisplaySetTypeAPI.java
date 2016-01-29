package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoDisplaySetTypeAPI extends HollowObjectTypeAPI {

    private final VideoDisplaySetDelegateLookupImpl delegateLookupImpl;

    VideoDisplaySetTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "topNodeId",
            "sets"
        });
        this.delegateLookupImpl = new VideoDisplaySetDelegateLookupImpl(this);
    }

    public long getTopNodeId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("VideoDisplaySet", ordinal, "topNodeId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getTopNodeIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("VideoDisplaySet", ordinal, "topNodeId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getSetsOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoDisplaySet", ordinal, "sets");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public VideoDisplaySetArrayOfSetsTypeAPI getSetsTypeAPI() {
        return getAPI().getVideoDisplaySetArrayOfSetsTypeAPI();
    }

    public VideoDisplaySetDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}