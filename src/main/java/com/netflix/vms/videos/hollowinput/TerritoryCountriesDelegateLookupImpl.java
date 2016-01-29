package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class TerritoryCountriesDelegateLookupImpl extends HollowObjectAbstractDelegate implements TerritoryCountriesDelegate {

    private final TerritoryCountriesTypeAPI typeAPI;

    public TerritoryCountriesDelegateLookupImpl(TerritoryCountriesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getCountryCodesOrdinal(int ordinal) {
        return typeAPI.getCountryCodesOrdinal(ordinal);
    }

    public int getTerritoryCodeOrdinal(int ordinal) {
        return typeAPI.getTerritoryCodeOrdinal(ordinal);
    }

    public TerritoryCountriesTypeAPI getTypeAPI() {
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