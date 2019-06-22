package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class AvailableAssetsTypeAPI extends HollowObjectTypeAPI {

    private final AvailableAssetsDelegateLookupImpl delegateLookupImpl;

    public AvailableAssetsTypeAPI(Gk2StatusAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "availableSubs",
            "availableDubs",
            "blockedSubs",
            "blockedDubs",
            "missingSubs",
            "missingDubs"
        });
        this.delegateLookupImpl = new AvailableAssetsDelegateLookupImpl(this);
    }

    public int getAvailableSubsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("AvailableAssets", ordinal, "availableSubs");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public SetOfStringTypeAPI getAvailableSubsTypeAPI() {
        return getAPI().getSetOfStringTypeAPI();
    }

    public int getAvailableDubsOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("AvailableAssets", ordinal, "availableDubs");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public SetOfStringTypeAPI getAvailableDubsTypeAPI() {
        return getAPI().getSetOfStringTypeAPI();
    }

    public int getBlockedSubsOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("AvailableAssets", ordinal, "blockedSubs");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public SetOfStringTypeAPI getBlockedSubsTypeAPI() {
        return getAPI().getSetOfStringTypeAPI();
    }

    public int getBlockedDubsOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("AvailableAssets", ordinal, "blockedDubs");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public SetOfStringTypeAPI getBlockedDubsTypeAPI() {
        return getAPI().getSetOfStringTypeAPI();
    }

    public int getMissingSubsOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("AvailableAssets", ordinal, "missingSubs");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public SetOfStringTypeAPI getMissingSubsTypeAPI() {
        return getAPI().getSetOfStringTypeAPI();
    }

    public int getMissingDubsOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("AvailableAssets", ordinal, "missingDubs");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public SetOfStringTypeAPI getMissingDubsTypeAPI() {
        return getAPI().getSetOfStringTypeAPI();
    }

    public AvailableAssetsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public Gk2StatusAPI getAPI() {
        return (Gk2StatusAPI) api;
    }

}