package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ContractDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ContractDelegate {

    private final Long contractId;
    private final Boolean original;
    private final int cupTokenOrdinal;
    private final Boolean dayOfBroadcast;
    private final Long prePromotionDays;
    private final Boolean dayAfterBroadcast;
    private final int disallowedAssetBundlesOrdinal;
    private ContractTypeAPI typeAPI;

    public ContractDelegateCachedImpl(ContractTypeAPI typeAPI, int ordinal) {
        this.contractId = typeAPI.getContractIdBoxed(ordinal);
        this.original = typeAPI.getOriginalBoxed(ordinal);
        this.cupTokenOrdinal = typeAPI.getCupTokenOrdinal(ordinal);
        this.dayOfBroadcast = typeAPI.getDayOfBroadcastBoxed(ordinal);
        this.prePromotionDays = typeAPI.getPrePromotionDaysBoxed(ordinal);
        this.dayAfterBroadcast = typeAPI.getDayAfterBroadcastBoxed(ordinal);
        this.disallowedAssetBundlesOrdinal = typeAPI.getDisallowedAssetBundlesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getContractId(int ordinal) {
        if(contractId == null)
            return Long.MIN_VALUE;
        return contractId.longValue();
    }

    public Long getContractIdBoxed(int ordinal) {
        return contractId;
    }

    public boolean getOriginal(int ordinal) {
        if(original == null)
            return false;
        return original.booleanValue();
    }

    public Boolean getOriginalBoxed(int ordinal) {
        return original;
    }

    public int getCupTokenOrdinal(int ordinal) {
        return cupTokenOrdinal;
    }

    public boolean getDayOfBroadcast(int ordinal) {
        if(dayOfBroadcast == null)
            return false;
        return dayOfBroadcast.booleanValue();
    }

    public Boolean getDayOfBroadcastBoxed(int ordinal) {
        return dayOfBroadcast;
    }

    public long getPrePromotionDays(int ordinal) {
        if(prePromotionDays == null)
            return Long.MIN_VALUE;
        return prePromotionDays.longValue();
    }

    public Long getPrePromotionDaysBoxed(int ordinal) {
        return prePromotionDays;
    }

    public boolean getDayAfterBroadcast(int ordinal) {
        if(dayAfterBroadcast == null)
            return false;
        return dayAfterBroadcast.booleanValue();
    }

    public Boolean getDayAfterBroadcastBoxed(int ordinal) {
        return dayAfterBroadcast;
    }

    public int getDisallowedAssetBundlesOrdinal(int ordinal) {
        return disallowedAssetBundlesOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public ContractTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ContractTypeAPI) typeAPI;
    }

}