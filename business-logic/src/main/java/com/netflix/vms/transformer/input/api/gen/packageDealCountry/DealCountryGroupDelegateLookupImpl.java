package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DealCountryGroupDelegateLookupImpl extends HollowObjectAbstractDelegate implements DealCountryGroupDelegate {

    private final DealCountryGroupTypeAPI typeAPI;

    public DealCountryGroupDelegateLookupImpl(DealCountryGroupTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getDealId(int ordinal) {
        ordinal = typeAPI.getDealIdOrdinal(ordinal);
        return ordinal == -1 ? Long.MIN_VALUE : typeAPI.getAPI().getLongTypeAPI().getValue(ordinal);
    }

    public Long getDealIdBoxed(int ordinal) {
        ordinal = typeAPI.getDealIdOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getLongTypeAPI().getValueBoxed(ordinal);
    }

    public int getDealIdOrdinal(int ordinal) {
        return typeAPI.getDealIdOrdinal(ordinal);
    }

    public int getCountryWindowOrdinal(int ordinal) {
        return typeAPI.getCountryWindowOrdinal(ordinal);
    }

    public DealCountryGroupTypeAPI getTypeAPI() {
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