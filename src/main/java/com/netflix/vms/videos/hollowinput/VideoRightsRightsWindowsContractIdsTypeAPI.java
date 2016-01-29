package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoRightsRightsWindowsContractIdsTypeAPI extends HollowObjectTypeAPI {

    private final VideoRightsRightsWindowsContractIdsDelegateLookupImpl delegateLookupImpl;

    VideoRightsRightsWindowsContractIdsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "value"
        });
        this.delegateLookupImpl = new VideoRightsRightsWindowsContractIdsDelegateLookupImpl(this);
    }

    public long getValue(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("VideoRightsRightsWindowsContractIds", ordinal, "value");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getValueBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("VideoRightsRightsWindowsContractIds", ordinal, "value");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public VideoRightsRightsWindowsContractIdsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}