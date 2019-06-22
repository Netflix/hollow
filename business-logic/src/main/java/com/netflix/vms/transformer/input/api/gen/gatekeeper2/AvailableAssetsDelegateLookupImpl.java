package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class AvailableAssetsDelegateLookupImpl extends HollowObjectAbstractDelegate implements AvailableAssetsDelegate {

    private final AvailableAssetsTypeAPI typeAPI;

    public AvailableAssetsDelegateLookupImpl(AvailableAssetsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getAvailableSubsOrdinal(int ordinal) {
        return typeAPI.getAvailableSubsOrdinal(ordinal);
    }

    public int getAvailableDubsOrdinal(int ordinal) {
        return typeAPI.getAvailableDubsOrdinal(ordinal);
    }

    public int getBlockedSubsOrdinal(int ordinal) {
        return typeAPI.getBlockedSubsOrdinal(ordinal);
    }

    public int getBlockedDubsOrdinal(int ordinal) {
        return typeAPI.getBlockedDubsOrdinal(ordinal);
    }

    public int getMissingSubsOrdinal(int ordinal) {
        return typeAPI.getMissingSubsOrdinal(ordinal);
    }

    public int getMissingDubsOrdinal(int ordinal) {
        return typeAPI.getMissingDubsOrdinal(ordinal);
    }

    public AvailableAssetsTypeAPI getTypeAPI() {
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