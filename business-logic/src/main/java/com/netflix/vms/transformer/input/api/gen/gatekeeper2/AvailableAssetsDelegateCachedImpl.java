package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class AvailableAssetsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate,
        AvailableAssetsDelegate {

    private final int availableSubsOrdinal;
    private final int availableDubsOrdinal;
    private final int blockedSubsOrdinal;
    private final int blockedDubsOrdinal;
    private final int missingSubsOrdinal;
    private final int missingDubsOrdinal;
    private AvailableAssetsTypeAPI typeAPI;

    public AvailableAssetsDelegateCachedImpl(AvailableAssetsTypeAPI typeAPI, int ordinal) {
        this.availableSubsOrdinal = typeAPI.getAvailableSubsOrdinal(ordinal);
        this.availableDubsOrdinal = typeAPI.getAvailableDubsOrdinal(ordinal);
        this.blockedSubsOrdinal = typeAPI.getBlockedSubsOrdinal(ordinal);
        this.blockedDubsOrdinal = typeAPI.getBlockedDubsOrdinal(ordinal);
        this.missingSubsOrdinal = typeAPI.getMissingSubsOrdinal(ordinal);
        this.missingDubsOrdinal = typeAPI.getMissingDubsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getAvailableSubsOrdinal(int ordinal) {
        return availableSubsOrdinal;
    }

    public int getAvailableDubsOrdinal(int ordinal) {
        return availableDubsOrdinal;
    }

    public int getBlockedSubsOrdinal(int ordinal) {
        return blockedSubsOrdinal;
    }

    public int getBlockedDubsOrdinal(int ordinal) {
        return blockedDubsOrdinal;
    }

    public int getMissingSubsOrdinal(int ordinal) {
        return missingSubsOrdinal;
    }

    public int getMissingDubsOrdinal(int ordinal) {
        return missingDubsOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public AvailableAssetsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (AvailableAssetsTypeAPI) typeAPI;
    }

}