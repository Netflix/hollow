package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class ContractTypeAPI extends HollowObjectTypeAPI {

    private final ContractDelegateLookupImpl delegateLookupImpl;

    ContractTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "contractId",
            "original",
            "cupToken",
            "dayOfBroadcast",
            "prePromotionDays",
            "dayAfterBroadcast",
            "disallowedAssetBundles"
        });
        this.delegateLookupImpl = new ContractDelegateLookupImpl(this);
    }

    public long getContractId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Contract", ordinal, "contractId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getContractIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Contract", ordinal, "contractId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public boolean getOriginal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("Contract", ordinal, "original") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]) == Boolean.TRUE;
    }

    public Boolean getOriginalBoxed(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("Contract", ordinal, "original");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]);
    }



    public int getCupTokenOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Contract", ordinal, "cupToken");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getCupTokenTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public boolean getDayOfBroadcast(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleBoolean("Contract", ordinal, "dayOfBroadcast") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]) == Boolean.TRUE;
    }

    public Boolean getDayOfBroadcastBoxed(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleBoolean("Contract", ordinal, "dayOfBroadcast");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]);
    }



    public long getPrePromotionDays(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleLong("Contract", ordinal, "prePromotionDays");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
    }

    public Long getPrePromotionDaysBoxed(int ordinal) {
        long l;
        if(fieldIndex[4] == -1) {
            l = missingDataHandler().handleLong("Contract", ordinal, "prePromotionDays");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[4]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public boolean getDayAfterBroadcast(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleBoolean("Contract", ordinal, "dayAfterBroadcast") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]) == Boolean.TRUE;
    }

    public Boolean getDayAfterBroadcastBoxed(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleBoolean("Contract", ordinal, "dayAfterBroadcast");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]);
    }



    public int getDisallowedAssetBundlesOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("Contract", ordinal, "disallowedAssetBundles");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public DisallowedAssetBundlesListTypeAPI getDisallowedAssetBundlesTypeAPI() {
        return getAPI().getDisallowedAssetBundlesListTypeAPI();
    }

    public ContractDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}