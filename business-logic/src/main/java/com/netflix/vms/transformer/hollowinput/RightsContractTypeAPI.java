package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RightsContractTypeAPI extends HollowObjectTypeAPI {

    private final RightsContractDelegateLookupImpl delegateLookupImpl;

    RightsContractTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "assets",
            "contractId",
            "packageId",
            "packages"
        });
        this.delegateLookupImpl = new RightsContractDelegateLookupImpl(this);
    }

    public int getAssetsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("RightsContract", ordinal, "assets");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public ListOfRightsContractAssetTypeAPI getAssetsTypeAPI() {
        return getAPI().getListOfRightsContractAssetTypeAPI();
    }

    public long getContractId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("RightsContract", ordinal, "contractId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getContractIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("RightsContract", ordinal, "contractId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getPackageId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("RightsContract", ordinal, "packageId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getPackageIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("RightsContract", ordinal, "packageId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getPackagesOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("RightsContract", ordinal, "packages");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public ListOfRightsContractPackageTypeAPI getPackagesTypeAPI() {
        return getAPI().getListOfRightsContractPackageTypeAPI();
    }

    public RightsContractDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}