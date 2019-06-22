package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsWindowContractDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RightsWindowContractDelegate {

    private final int assetsOrdinal;
    private final Long dealId;
    private final Long packageId;
    private final int packagesOrdinal;
    private final Boolean download;
    private RightsWindowContractTypeAPI typeAPI;

    public RightsWindowContractDelegateCachedImpl(RightsWindowContractTypeAPI typeAPI, int ordinal) {
        this.assetsOrdinal = typeAPI.getAssetsOrdinal(ordinal);
        this.dealId = typeAPI.getDealIdBoxed(ordinal);
        this.packageId = typeAPI.getPackageIdBoxed(ordinal);
        this.packagesOrdinal = typeAPI.getPackagesOrdinal(ordinal);
        this.download = typeAPI.getDownloadBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getAssetsOrdinal(int ordinal) {
        return assetsOrdinal;
    }

    public long getDealId(int ordinal) {
        if(dealId == null)
            return Long.MIN_VALUE;
        return dealId.longValue();
    }

    public Long getDealIdBoxed(int ordinal) {
        return dealId;
    }

    public long getPackageId(int ordinal) {
        if(packageId == null)
            return Long.MIN_VALUE;
        return packageId.longValue();
    }

    public Long getPackageIdBoxed(int ordinal) {
        return packageId;
    }

    public int getPackagesOrdinal(int ordinal) {
        return packagesOrdinal;
    }

    public boolean getDownload(int ordinal) {
        if(download == null)
            return false;
        return download.booleanValue();
    }

    public Boolean getDownloadBoxed(int ordinal) {
        return download;
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