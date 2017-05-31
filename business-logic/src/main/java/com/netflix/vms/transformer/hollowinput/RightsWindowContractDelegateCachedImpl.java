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
    private final int assetSetIdOrdinal;
   private RightsWindowContractTypeAPI typeAPI;

    public RightsWindowContractDelegateCachedImpl(RightsWindowContractTypeAPI typeAPI, int ordinal) {
        this.contractId = typeAPI.getContractIdBoxed(ordinal);
        this.download = typeAPI.getDownloadBoxed(ordinal);
        this.assetSetIdOrdinal = typeAPI.getAssetSetIdOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getContractId(int ordinal) {
        return contractId.longValue();
    }

    public Long getContractIdBoxed(int ordinal) {
        return contractId;
    }

    public boolean getDownload(int ordinal) {
        return download.booleanValue();
    }

    public Boolean getDownloadBoxed(int ordinal) {
        return download;
    }

    public int getAssetSetIdOrdinal(int ordinal) {
        return assetSetIdOrdinal;
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