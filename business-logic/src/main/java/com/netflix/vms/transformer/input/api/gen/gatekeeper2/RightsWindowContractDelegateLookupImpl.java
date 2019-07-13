package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsWindowContractDelegateLookupImpl extends HollowObjectAbstractDelegate implements
        RightsWindowContractDelegate {

    private final RightsWindowContractTypeAPI typeAPI;

    public RightsWindowContractDelegateLookupImpl(RightsWindowContractTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getAssetsOrdinal(int ordinal) {
        return typeAPI.getAssetsOrdinal(ordinal);
    }

    public long getDealId(int ordinal) {
        return typeAPI.getDealId(ordinal);
    }

    public Long getDealIdBoxed(int ordinal) {
        return typeAPI.getDealIdBoxed(ordinal);
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

    public boolean getDownload(int ordinal) {
        return typeAPI.getDownload(ordinal);
    }

    public Boolean getDownloadBoxed(int ordinal) {
        return typeAPI.getDownloadBoxed(ordinal);
    }

    public RightsWindowContractTypeAPI getTypeAPI() {
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