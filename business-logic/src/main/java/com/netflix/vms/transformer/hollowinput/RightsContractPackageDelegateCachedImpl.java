package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsContractPackageDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RightsContractPackageDelegate {

    private final Long packageId;
    private final Boolean primary;
    private RightsContractPackageTypeAPI typeAPI;

    public RightsContractPackageDelegateCachedImpl(RightsContractPackageTypeAPI typeAPI, int ordinal) {
        this.packageId = typeAPI.getPackageIdBoxed(ordinal);
        this.primary = typeAPI.getPrimaryBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getPackageId(int ordinal) {
        if(packageId == null)
            return Long.MIN_VALUE;
        return packageId.longValue();
    }

    public Long getPackageIdBoxed(int ordinal) {
        return packageId;
    }

    public boolean getPrimary(int ordinal) {
        if(primary == null)
            return false;
        return primary.booleanValue();
    }

    public Boolean getPrimaryBoxed(int ordinal) {
        return primary;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public RightsContractPackageTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RightsContractPackageTypeAPI) typeAPI;
    }

}