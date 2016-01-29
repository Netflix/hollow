package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class TerritoryCountriesTypeAPI extends HollowObjectTypeAPI {

    private final TerritoryCountriesDelegateLookupImpl delegateLookupImpl;

    TerritoryCountriesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "countryCodes",
            "territoryCode"
        });
        this.delegateLookupImpl = new TerritoryCountriesDelegateLookupImpl(this);
    }

    public int getCountryCodesOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("TerritoryCountries", ordinal, "countryCodes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public TerritoryCountriesArrayOfCountryCodesTypeAPI getCountryCodesTypeAPI() {
        return getAPI().getTerritoryCountriesArrayOfCountryCodesTypeAPI();
    }

    public int getTerritoryCodeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("TerritoryCountries", ordinal, "territoryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getTerritoryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public TerritoryCountriesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}