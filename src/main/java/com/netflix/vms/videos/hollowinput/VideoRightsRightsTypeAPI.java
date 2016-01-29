package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoRightsRightsTypeAPI extends HollowObjectTypeAPI {

    private final VideoRightsRightsDelegateLookupImpl delegateLookupImpl;

    VideoRightsRightsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
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

    public VideoRightsRightsArrayOfWindowsTypeAPI getWindowsTypeAPI() {
        return getAPI().getVideoRightsRightsArrayOfWindowsTypeAPI();
    }

    public int getContractsOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsRights", ordinal, "contracts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public VideoRightsRightsArrayOfContractsTypeAPI getContractsTypeAPI() {
        return getAPI().getVideoRightsRightsArrayOfContractsTypeAPI();
    }

    public VideoRightsRightsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}