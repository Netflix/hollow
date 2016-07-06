package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class ContractDelegateLookupImpl extends HollowObjectAbstractDelegate implements ContractDelegate {

    private final ContractTypeAPI typeAPI;

    public ContractDelegateLookupImpl(ContractTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getContractId(int ordinal) {
        return typeAPI.getContractId(ordinal);
    }

    public Long getContractIdBoxed(int ordinal) {
        return typeAPI.getContractIdBoxed(ordinal);
    }

    public boolean getOriginal(int ordinal) {
        return typeAPI.getOriginal(ordinal);
    }

    public Boolean getOriginalBoxed(int ordinal) {
        return typeAPI.getOriginalBoxed(ordinal);
    }

    public int getCupTokenOrdinal(int ordinal) {
        return typeAPI.getCupTokenOrdinal(ordinal);
    }

    public boolean getDayOfBroadcast(int ordinal) {
        return typeAPI.getDayOfBroadcast(ordinal);
    }

    public Boolean getDayOfBroadcastBoxed(int ordinal) {
        return typeAPI.getDayOfBroadcastBoxed(ordinal);
    }

    public long getPrePromotionDays(int ordinal) {
        return typeAPI.getPrePromotionDays(ordinal);
    }

    public Long getPrePromotionDaysBoxed(int ordinal) {
        return typeAPI.getPrePromotionDaysBoxed(ordinal);
    }

    public boolean getDayAfterBroadcast(int ordinal) {
        return typeAPI.getDayAfterBroadcast(ordinal);
    }

    public Boolean getDayAfterBroadcastBoxed(int ordinal) {
        return typeAPI.getDayAfterBroadcastBoxed(ordinal);
    }

    public int getDisallowedAssetBundlesOrdinal(int ordinal) {
        return typeAPI.getDisallowedAssetBundlesOrdinal(ordinal);
    }

    public ContractTypeAPI getTypeAPI() {
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