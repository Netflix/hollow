package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DamMerchStillsMomentDelegateLookupImpl extends HollowObjectAbstractDelegate implements DamMerchStillsMomentDelegate {

    private final DamMerchStillsMomentTypeAPI typeAPI;

    public DamMerchStillsMomentDelegateLookupImpl(DamMerchStillsMomentTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getPackageIdOrdinal(int ordinal) {
        return typeAPI.getPackageIdOrdinal(ordinal);
    }

    public int getStillTSOrdinal(int ordinal) {
        return typeAPI.getStillTSOrdinal(ordinal);
    }

    public DamMerchStillsMomentTypeAPI getTypeAPI() {
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