package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class CountryVideoDisplaySetDelegateLookupImpl extends HollowObjectAbstractDelegate implements CountryVideoDisplaySetDelegate {

    private final CountryVideoDisplaySetTypeAPI typeAPI;

    public CountryVideoDisplaySetDelegateLookupImpl(CountryVideoDisplaySetTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return typeAPI.getCountryCodeOrdinal(ordinal);
    }

    public int getSetTypeOrdinal(int ordinal) {
        return typeAPI.getSetTypeOrdinal(ordinal);
    }

    public int getChildrenOrdinal(int ordinal) {
        return typeAPI.getChildrenOrdinal(ordinal);
    }

    public CountryVideoDisplaySetTypeAPI getTypeAPI() {
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