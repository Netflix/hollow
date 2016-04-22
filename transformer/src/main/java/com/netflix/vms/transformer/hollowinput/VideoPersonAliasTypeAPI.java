package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoPersonAliasTypeAPI extends HollowObjectTypeAPI {

    private final VideoPersonAliasDelegateLookupImpl delegateLookupImpl;

    VideoPersonAliasTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "aliasId"
        });
        this.delegateLookupImpl = new VideoPersonAliasDelegateLookupImpl(this);
    }

    public long getAliasId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("VideoPersonAlias", ordinal, "aliasId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getAliasIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("VideoPersonAlias", ordinal, "aliasId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public VideoPersonAliasDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}