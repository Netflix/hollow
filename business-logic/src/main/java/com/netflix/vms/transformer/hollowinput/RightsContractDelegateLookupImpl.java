package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsContractDelegateLookupImpl extends HollowObjectAbstractDelegate implements RightsContractDelegate {

    private final RightsContractTypeAPI typeAPI;

    public RightsContractDelegateLookupImpl(RightsContractTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getAssetsOrdinal(int ordinal) {
        return typeAPI.getAssetsOrdinal(ordinal);
    }

    public long getContractId(int ordinal) {
        return typeAPI.getContractId(ordinal);
    }

    public Long getContractIdBoxed(int ordinal) {
        return typeAPI.getContractIdBoxed(ordinal);
    }

    public long getPackageId(int ordinal) {
        return typeAPI.getPackageId(ordinal);
    }

    public Long getPackageIdBoxed(int ordinal) {
        return typeAPI.getPackageIdBoxed(ordinal);
    }

    public int getPackagesOrdinal(int ordinal) {
        return typeAPI.getPackagesOrdinal(ordinal);
    }

    public RightsContractTypeAPI getTypeAPI() {
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