package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class RightsWindowContractDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RightsWindowContractDelegate {

    private final Long contractId;
    private final Boolean download;
    private final Long packageId;
    private final int assetsOrdinal;
    private final int packagesOrdinal;
    private RightsWindowContractTypeAPI typeAPI;

    public RightsWindowContractDelegateCachedImpl(RightsWindowContractTypeAPI typeAPI, int ordinal) {
        this.contractId = typeAPI.getContractIdBoxed(ordinal);
        this.download = typeAPI.getDownloadBoxed(ordinal);
        this.packageId = typeAPI.getPackageIdBoxed(ordinal);
        this.assetsOrdinal = typeAPI.getAssetsOrdinal(ordinal);
        this.packagesOrdinal = typeAPI.getPackagesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getContractId(int ordinal) {
        if(contractId == null)
            return Long.MIN_VALUE;
        return contractId.longValue();
    }

    public Long getContractIdBoxed(int ordinal) {
        return contractId;
    }

    public boolean getDownload(int ordinal) {
        if(download == null)
            return false;
        return download.booleanValue();
    }

    public Boolean getDownloadBoxed(int ordinal) {
        return download;
    }

    public long getPackageId(int ordinal) {
        if(packageId == null)
            return Long.MIN_VALUE;
        return packageId.longValue();
    }

    public Long getPackageIdBoxed(int ordinal) {
        return packageId;
    }

    public int getAssetsOrdinal(int ordinal) {
        return assetsOrdinal;
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

    public RightsWindowContractTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RightsWindowContractTypeAPI) typeAPI;
    }

}