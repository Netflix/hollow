package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class CountryVideoDisplaySetTypeAPI extends HollowObjectTypeAPI {

    private final CountryVideoDisplaySetDelegateLookupImpl delegateLookupImpl;

    CountryVideoDisplaySetTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "countryCode",
            "setType",
            "children"
        });
        this.delegateLookupImpl = new CountryVideoDisplaySetDelegateLookupImpl(this);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("CountryVideoDisplaySet", ordinal, "countryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getCountryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getSetTypeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("CountryVideoDisplaySet", ordinal, "setType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getSetTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getChildrenOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("CountryVideoDisplaySet", ordinal, "children");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public SeasonListTypeAPI getChildrenTypeAPI() {
        return getAPI().getSeasonListTypeAPI();
    }

    public CountryVideoDisplaySetDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}