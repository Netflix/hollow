package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TerritoryCountriesDelegateLookupImpl extends HollowObjectAbstractDelegate implements TerritoryCountriesDelegate {

    private final TerritoryCountriesTypeAPI typeAPI;

    public TerritoryCountriesDelegateLookupImpl(TerritoryCountriesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getTerritoryCodeOrdinal(int ordinal) {
        return typeAPI.getTerritoryCodeOrdinal(ordinal);
    }

    public int getCountryCodesOrdinal(int ordinal) {
        return typeAPI.getCountryCodesOrdinal(ordinal);
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