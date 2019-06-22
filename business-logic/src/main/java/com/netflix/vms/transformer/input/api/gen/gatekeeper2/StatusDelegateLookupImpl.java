package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StatusDelegateLookupImpl extends HollowObjectAbstractDelegate implements StatusDelegate {

    private final StatusTypeAPI typeAPI;

    public StatusDelegateLookupImpl(StatusTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        return typeAPI.getMovieId(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        return typeAPI.getMovieIdBoxed(ordinal);
    }

    public String getCountryCode(int ordinal) {
        ordinal = typeAPI.getCountryCodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isCountryCodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getCountryCodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return typeAPI.getCountryCodeOrdinal(ordinal);
    }

    public int getRightsOrdinal(int ordinal) {
        return typeAPI.getRightsOrdinal(ordinal);
    }

    public int getFlagsOrdinal(int ordinal) {
        return typeAPI.getFlagsOrdinal(ordinal);
    }

    public int getAvailableAssetsOrdinal(int ordinal) {
        return typeAPI.getAvailableAssetsOrdinal(ordinal);
    }

    public int getHierarchyInfoOrdinal(int ordinal) {
        return typeAPI.getHierarchyInfoOrdinal(ordinal);
    }

    public StatusTypeAPI getTypeAPI() {
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