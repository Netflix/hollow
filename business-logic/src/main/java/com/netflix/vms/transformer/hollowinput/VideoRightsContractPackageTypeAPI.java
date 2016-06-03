package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoRightsContractPackageTypeAPI extends HollowObjectTypeAPI {

    private final VideoRightsContractPackageDelegateLookupImpl delegateLookupImpl;

    VideoRightsContractPackageTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "packageId",
            "primary"
        });
        this.delegateLookupImpl = new VideoRightsContractPackageDelegateLookupImpl(this);
    }

    public long getPackageId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("VideoRightsContractPackage", ordinal, "packageId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getPackageIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("VideoRightsContractPackage", ordinal, "packageId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public boolean getPrimary(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("VideoRightsContractPackage", ordinal, "primary") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]) == Boolean.TRUE;
    }

    public Boolean getPrimaryBoxed(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("VideoRightsContractPackage", ordinal, "primary");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]);
    }



    public VideoRightsContractPackageDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}