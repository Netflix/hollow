package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TerritoryCountriesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, TerritoryCountriesDelegate {

    private final int territoryCodeOrdinal;
    private final int countryCodesOrdinal;
    private TerritoryCountriesTypeAPI typeAPI;

    public TerritoryCountriesDelegateCachedImpl(TerritoryCountriesTypeAPI typeAPI, int ordinal) {
        this.territoryCodeOrdinal = typeAPI.getTerritoryCodeOrdinal(ordinal);
        this.countryCodesOrdinal = typeAPI.getCountryCodesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getTerritoryCodeOrdinal(int ordinal) {
        return territoryCodeOrdinal;
    }

    public int getCountryCodesOrdinal(int ordinal) {
        return countryCodesOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public TerritoryCountriesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (TerritoryCountriesTypeAPI) typeAPI;
    }

}