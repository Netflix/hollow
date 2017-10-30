package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class VideoRatingAdvisoryIdTypeAPI extends HollowObjectTypeAPI {

    private final VideoRatingAdvisoryIdDelegateLookupImpl delegateLookupImpl;

    public VideoRatingAdvisoryIdTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "value"
        });
        this.delegateLookupImpl = new VideoRatingAdvisoryIdDelegateLookupImpl(this);
    }

    public long getValue(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("VideoRatingAdvisoryId", ordinal, "value");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getValueBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("VideoRatingAdvisoryId", ordinal, "value");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public VideoRatingAdvisoryIdDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}