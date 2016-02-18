package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class TerritoryCountriesTypeAPI extends HollowObjectTypeAPI {

    private final TerritoryCountriesDelegateLookupImpl delegateLookupImpl;

    TerritoryCountriesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "territoryCode",
            "countryCodes"
        });
        this.delegateLookupImpl = new TerritoryCountriesDelegateLookupImpl(this);
    }

    public int getTerritoryCodeOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("TerritoryCountries", ordinal, "territoryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getTerritoryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getCountryCodesOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("TerritoryCountries", ordinal, "countryCodes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public ISOCountryListTypeAPI getCountryCodesTypeAPI() {
        return getAPI().getISOCountryListTypeAPI();
    }

    public TerritoryCountriesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}