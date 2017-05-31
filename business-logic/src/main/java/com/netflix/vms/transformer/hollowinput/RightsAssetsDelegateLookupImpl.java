package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsAssetsDelegateLookupImpl extends HollowObjectAbstractDelegate implements RightsAssetsDelegate {

    private final RightsAssetsTypeAPI typeAPI;

    public RightsAssetsDelegateLookupImpl(RightsAssetsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getAssetSetIdOrdinal(int ordinal) {
        return typeAPI.getAssetSetIdOrdinal(ordinal);
    }

    public int getAssetsOrdinal(int ordinal) {
        return typeAPI.getAssetsOrdinal(ordinal);
    }

    public RightsAssetsTypeAPI getTypeAPI() {
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