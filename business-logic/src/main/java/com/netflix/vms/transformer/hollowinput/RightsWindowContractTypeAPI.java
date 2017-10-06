package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class RightsWindowContractTypeAPI extends HollowObjectTypeAPI {

    private final RightsWindowContractDelegateLookupImpl delegateLookupImpl;

    RightsWindowContractTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "contractId",
            "download",
            "packageId",
            "assets",
            "packages"
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



    public long getPackageId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("RightsWindowContract", ordinal, "packageId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getPackageIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("RightsWindowContract", ordinal, "packageId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getAssetsOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("RightsWindowContract", ordinal, "assets");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public ListOfRightsContractAssetTypeAPI getAssetsTypeAPI() {
        return getAPI().getListOfRightsContractAssetTypeAPI();
    }

    public int getPackagesOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("RightsWindowContract", ordinal, "packages");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public ListOfRightsContractPackageTypeAPI getPackagesTypeAPI() {
        return getAPI().getListOfRightsContractPackageTypeAPI();
    }

    public RightsWindowContractDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}