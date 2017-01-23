package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class RightsContractDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RightsContractDelegate {

    private final int assetsOrdinal;
    private final Long contractId;
    private final Long packageId;
    private final int packagesOrdinal;
   private RightsContractTypeAPI typeAPI;

    public RightsContractDelegateCachedImpl(RightsContractTypeAPI typeAPI, int ordinal) {
        this.assetsOrdinal = typeAPI.getAssetsOrdinal(ordinal);
        this.contractId = typeAPI.getContractIdBoxed(ordinal);
        this.packageId = typeAPI.getPackageIdBoxed(ordinal);
        this.packagesOrdinal = typeAPI.getPackagesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getAssetsOrdinal(int ordinal) {
        return assetsOrdinal;
    }

    public long getContractId(int ordinal) {
        return contractId.longValue();
    }

    public Long getContractIdBoxed(int ordinal) {
        return contractId;
    }

    public long getPackageId(int ordinal) {
        return packageId.longValue();
    }

    public Long getPackageIdBoxed(int ordinal) {
        return packageId;
    }

    public int getPackagesOrdinal(int ordinal) {
        return packagesOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public RightsContractTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RightsContractTypeAPI) typeAPI;
    }

}