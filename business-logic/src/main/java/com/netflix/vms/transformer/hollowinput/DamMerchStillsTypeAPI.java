package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class DamMerchStillsTypeAPI extends HollowObjectTypeAPI {

    private final DamMerchStillsDelegateLookupImpl delegateLookupImpl;

    DamMerchStillsTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "assetId",
            "moment"
        });
        this.delegateLookupImpl = new DamMerchStillsDelegateLookupImpl(this);
    }

    public int getAssetIdOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("DamMerchStills", ordinal, "assetId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getAssetIdTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getMomentOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("DamMerchStills", ordinal, "moment");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public DamMerchStillsMomentTypeAPI getMomentTypeAPI() {
        return getAPI().getDamMerchStillsMomentTypeAPI();
    }

    public DamMerchStillsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}