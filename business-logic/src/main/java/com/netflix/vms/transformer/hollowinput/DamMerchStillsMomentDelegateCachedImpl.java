package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DamMerchStillsMomentDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, DamMerchStillsMomentDelegate {

    private final int packageIdOrdinal;
    private final int stillTSOrdinal;
   private DamMerchStillsMomentTypeAPI typeAPI;

    public DamMerchStillsMomentDelegateCachedImpl(DamMerchStillsMomentTypeAPI typeAPI, int ordinal) {
        this.packageIdOrdinal = typeAPI.getPackageIdOrdinal(ordinal);
        this.stillTSOrdinal = typeAPI.getStillTSOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getPackageIdOrdinal(int ordinal) {
        return packageIdOrdinal;
    }

    public int getStillTSOrdinal(int ordinal) {
        return stillTSOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public DamMerchStillsMomentTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (DamMerchStillsMomentTypeAPI) typeAPI;
    }

}