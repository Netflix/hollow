package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class DamMerchStillsMomentTypeAPI extends HollowObjectTypeAPI {

    private final DamMerchStillsMomentDelegateLookupImpl delegateLookupImpl;

    public DamMerchStillsMomentTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "packageId",
            "stillTS"
        });
        this.delegateLookupImpl = new DamMerchStillsMomentDelegateLookupImpl(this);
    }

    public int getPackageIdOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("DamMerchStillsMoment", ordinal, "packageId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getPackageIdTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getStillTSOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("DamMerchStillsMoment", ordinal, "stillTS");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getStillTSTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public DamMerchStillsMomentDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}