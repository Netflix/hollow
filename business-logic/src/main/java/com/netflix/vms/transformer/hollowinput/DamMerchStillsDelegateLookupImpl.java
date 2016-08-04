package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class DamMerchStillsDelegateLookupImpl extends HollowObjectAbstractDelegate implements DamMerchStillsDelegate {

    private final DamMerchStillsTypeAPI typeAPI;

    public DamMerchStillsDelegateLookupImpl(DamMerchStillsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getAssetIdOrdinal(int ordinal) {
        return typeAPI.getAssetIdOrdinal(ordinal);
    }

    public int getMomentOrdinal(int ordinal) {
        return typeAPI.getMomentOrdinal(ordinal);
    }

    public DamMerchStillsTypeAPI getTypeAPI() {
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