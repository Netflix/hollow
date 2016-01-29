package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class StreamProfileGroupsTypeAPI extends HollowObjectTypeAPI {

    private final StreamProfileGroupsDelegateLookupImpl delegateLookupImpl;

    StreamProfileGroupsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "groupName",
            "streamProfileIds"
        });
        this.delegateLookupImpl = new StreamProfileGroupsDelegateLookupImpl(this);
    }

    public int getGroupNameOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamProfileGroups", ordinal, "groupName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getGroupNameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getStreamProfileIdsOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamProfileGroups", ordinal, "streamProfileIds");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StreamProfileGroupsArrayOfStreamProfileIdsTypeAPI getStreamProfileIdsTypeAPI() {
        return getAPI().getStreamProfileGroupsArrayOfStreamProfileIdsTypeAPI();
    }

    public StreamProfileGroupsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}