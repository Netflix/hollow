package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class DamMerchStillsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, DamMerchStillsDelegate {

    private final int assetIdOrdinal;
    private final int momentOrdinal;
   private DamMerchStillsTypeAPI typeAPI;

    public DamMerchStillsDelegateCachedImpl(DamMerchStillsTypeAPI typeAPI, int ordinal) {
        this.assetIdOrdinal = typeAPI.getAssetIdOrdinal(ordinal);
        this.momentOrdinal = typeAPI.getMomentOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getAssetIdOrdinal(int ordinal) {
        return assetIdOrdinal;
    }

    public int getMomentOrdinal(int ordinal) {
        return momentOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public DamMerchStillsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (DamMerchStillsTypeAPI) typeAPI;
    }

}