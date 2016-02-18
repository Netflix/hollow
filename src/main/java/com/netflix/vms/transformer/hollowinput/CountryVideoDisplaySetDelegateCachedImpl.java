package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class CountryVideoDisplaySetDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, CountryVideoDisplaySetDelegate {

    private final int countryCodeOrdinal;
    private final int setTypeOrdinal;
    private final int childrenOrdinal;
   private CountryVideoDisplaySetTypeAPI typeAPI;

    public CountryVideoDisplaySetDelegateCachedImpl(CountryVideoDisplaySetTypeAPI typeAPI, int ordinal) {
        this.countryCodeOrdinal = typeAPI.getCountryCodeOrdinal(ordinal);
        this.setTypeOrdinal = typeAPI.getSetTypeOrdinal(ordinal);
        this.childrenOrdinal = typeAPI.getChildrenOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return countryCodeOrdinal;
    }

    public int getSetTypeOrdinal(int ordinal) {
        return setTypeOrdinal;
    }

    public int getChildrenOrdinal(int ordinal) {
        return childrenOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public CountryVideoDisplaySetTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (CountryVideoDisplaySetTypeAPI) typeAPI;
    }

}