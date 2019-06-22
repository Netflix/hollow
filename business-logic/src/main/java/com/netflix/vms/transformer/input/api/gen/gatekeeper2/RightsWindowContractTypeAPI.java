package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class RightsWindowContractTypeAPI extends HollowObjectTypeAPI {

    private final RightsWindowContractDelegateLookupImpl delegateLookupImpl;

    public RightsWindowContractTypeAPI(Gk2StatusAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "assets",
            "dealId",
            "packageId",
            "packages",
            "download"
        });
        this.delegateLookupImpl = new RightsWindowContractDelegateLookupImpl(this);
    }

    public int getAssetsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("RightsWindowContract", ordinal, "assets");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public ListOfRightsContractAssetTypeAPI getAssetsTypeAPI() {
        return getAPI().getListOfRightsContractAssetTypeAPI();
    }

    public long getDealId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("RightsWindowContract", ordinal, "dealId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getDealIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("RightsWindowContract", ordinal, "dealId");
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



    public int getPackagesOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("RightsWindowContract", ordinal, "packages");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public ListOfRightsContractPackageTypeAPI getPackagesTypeAPI() {
        return getAPI().getListOfRightsContractPackageTypeAPI();
    }

    public boolean getDownload(int ordinal) {
        if(fieldIndex[4] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("RightsWindowContract", ordinal, "download"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]));
    }

    public Boolean getDownloadBoxed(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleBoolean("RightsWindowContract", ordinal, "download");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]);
    }



    public RightsWindowContractDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public Gk2StatusAPI getAPI() {
        return (Gk2StatusAPI) api;
    }

}