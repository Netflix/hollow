package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class TerritoryCountriesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, TerritoryCountriesDelegate {

    private final int countryCodesOrdinal;
    private final int territoryCodeOrdinal;
   private TerritoryCountriesTypeAPI typeAPI;

    public TerritoryCountriesDelegateCachedImpl(TerritoryCountriesTypeAPI typeAPI, int ordinal) {
        this.countryCodesOrdinal = typeAPI.getCountryCodesOrdinal(ordinal);
        this.territoryCodeOrdinal = typeAPI.getTerritoryCodeOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getCountryCodesOrdinal(int ordinal) {
        return countryCodesOrdinal;
    }

    public int getTerritoryCodeOrdinal(int ordinal) {
        return territoryCodeOrdinal;
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