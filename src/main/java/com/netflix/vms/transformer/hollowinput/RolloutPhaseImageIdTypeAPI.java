package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RolloutPhaseImageIdTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhaseImageIdDelegateLookupImpl delegateLookupImpl;

    RolloutPhaseImageIdTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "imageId"
        });
        this.delegateLookupImpl = new RolloutPhaseImageIdDelegateLookupImpl(this);
    }

    public long getImageId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("RolloutPhaseImageId", ordinal, "imageId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getImageIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("RolloutPhaseImageId", ordinal, "imageId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public RolloutPhaseImageIdDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}