package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class CdnDeploymentTypeAPI extends HollowObjectTypeAPI {

    private final CdnDeploymentDelegateLookupImpl delegateLookupImpl;

    public CdnDeploymentTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "originServerId",
            "directory",
            "originServer"
        });
        this.delegateLookupImpl = new CdnDeploymentDelegateLookupImpl(this);
    }

    public long getOriginServerId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("CdnDeployment", ordinal, "originServerId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getOriginServerIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("CdnDeployment", ordinal, "originServerId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getDirectoryOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("CdnDeployment", ordinal, "directory");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getDirectoryTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getOriginServerOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("CdnDeployment", ordinal, "originServer");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getOriginServerTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public CdnDeploymentDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}