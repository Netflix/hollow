package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class RightsWindowContractTypeAPI extends HollowObjectTypeAPI {

    private final RightsWindowContractDelegateLookupImpl delegateLookupImpl;

    RightsWindowContractTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "contractId",
            "download"
        });
        this.delegateLookupImpl = new RightsWindowContractDelegateLookupImpl(this);
    }

    public long getContractId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("RightsWindowContract", ordinal, "contractId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getContractIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("RightsWindowContract", ordinal, "contractId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public boolean getDownload(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("RightsWindowContract", ordinal, "download") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]) == Boolean.TRUE;
    }

    public Boolean getDownloadBoxed(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("RightsWindowContract", ordinal, "download");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]);
    }



    public RightsWindowContractDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}