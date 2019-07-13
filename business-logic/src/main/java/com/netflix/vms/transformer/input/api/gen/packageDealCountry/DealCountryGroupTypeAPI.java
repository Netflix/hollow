package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class DealCountryGroupTypeAPI extends HollowObjectTypeAPI {

    private final DealCountryGroupDelegateLookupImpl delegateLookupImpl;

    public DealCountryGroupTypeAPI(PackageDealCountryAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "dealId",
            "countryWindow"
        });
        this.delegateLookupImpl = new DealCountryGroupDelegateLookupImpl(this);
    }

    public int getDealIdOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("DealCountryGroup", ordinal, "dealId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public LongTypeAPI getDealIdTypeAPI() {
        return getAPI().getLongTypeAPI();
    }

    public int getCountryWindowOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("DealCountryGroup", ordinal, "countryWindow");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public MapOfStringToBooleanTypeAPI getCountryWindowTypeAPI() {
        return getAPI().getMapOfStringToBooleanTypeAPI();
    }

    public DealCountryGroupDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public PackageDealCountryAPI getAPI() {
        return (PackageDealCountryAPI) api;
    }

}