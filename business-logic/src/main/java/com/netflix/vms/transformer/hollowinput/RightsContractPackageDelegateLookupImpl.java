package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsContractPackageDelegateLookupImpl extends HollowObjectAbstractDelegate implements RightsContractPackageDelegate {

    private final RightsContractPackageTypeAPI typeAPI;

    public RightsContractPackageDelegateLookupImpl(RightsContractPackageTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getPackageId(int ordinal) {
        return typeAPI.getPackageId(ordinal);
    }

    public Long getPackageIdBoxed(int ordinal) {
        return typeAPI.getPackageIdBoxed(ordinal);
    }

    public boolean getPrimary(int ordinal) {
        return typeAPI.getPrimary(ordinal);
    }

    public Boolean getPrimaryBoxed(int ordinal) {
        return typeAPI.getPrimaryBoxed(ordinal);
    }

    public RightsContractPackageTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}