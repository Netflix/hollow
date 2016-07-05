package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoRightsRightsTypeAPI extends HollowObjectTypeAPI {

    private final VideoRightsRightsDelegateLookupImpl delegateLookupImpl;

    VideoRightsRightsTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "windows",
            "contracts"
        });
        this.delegateLookupImpl = new VideoRightsRightsDelegateLookupImpl(this);
    }

    public int getWindowsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsRights", ordinal, "windows");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public VideoRightsWindowsSetTypeAPI getWindowsTypeAPI() {
        return getAPI().getVideoRightsWindowsSetTypeAPI();
    }

    public int getContractsOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsRights", ordinal, "contracts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public VideoRightsContractSetTypeAPI getContractsTypeAPI() {
        return getAPI().getVideoRightsContractSetTypeAPI();
    }

    public VideoRightsRightsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}