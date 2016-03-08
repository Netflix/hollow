package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoArtWorkSourceAttributesTypeAPI extends HollowObjectTypeAPI {

    private final VideoArtWorkSourceAttributesDelegateLookupImpl delegateLookupImpl;

    VideoArtWorkSourceAttributesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "passthrough"
        });
        this.delegateLookupImpl = new VideoArtWorkSourceAttributesDelegateLookupImpl(this);
    }

    public int getPassthroughOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkSourceAttributes", ordinal, "passthrough");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public PassthroughDataTypeAPI getPassthroughTypeAPI() {
        return getAPI().getPassthroughDataTypeAPI();
    }

    public VideoArtWorkSourceAttributesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}