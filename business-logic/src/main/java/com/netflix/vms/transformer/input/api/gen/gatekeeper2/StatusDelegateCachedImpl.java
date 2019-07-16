package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class StatusDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, StatusDelegate {

    private final Long movieId;
    private final String countryCode;
    private final int countryCodeOrdinal;
    private final int rightsOrdinal;
    private final int flagsOrdinal;
    private final int availableAssetsOrdinal;
    private final int hierarchyInfoOrdinal;
    private StatusTypeAPI typeAPI;

    public StatusDelegateCachedImpl(StatusTypeAPI typeAPI, int ordinal) {
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.countryCodeOrdinal = typeAPI.getCountryCodeOrdinal(ordinal);
        int countryCodeTempOrdinal = countryCodeOrdinal;
        this.countryCode = countryCodeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(countryCodeTempOrdinal);
        this.rightsOrdinal = typeAPI.getRightsOrdinal(ordinal);
        this.flagsOrdinal = typeAPI.getFlagsOrdinal(ordinal);
        this.availableAssetsOrdinal = typeAPI.getAvailableAssetsOrdinal(ordinal);
        this.hierarchyInfoOrdinal = typeAPI.getHierarchyInfoOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        if(movieId == null)
            return Long.MIN_VALUE;
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public String getCountryCode(int ordinal) {
        return countryCode;
    }

    public boolean isCountryCodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return countryCode == null;
        return testValue.equals(countryCode);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return countryCodeOrdinal;
    }

    public int getRightsOrdinal(int ordinal) {
        return rightsOrdinal;
    }

    public int getFlagsOrdinal(int ordinal) {
        return flagsOrdinal;
    }

    public int getAvailableAssetsOrdinal(int ordinal) {
        return availableAssetsOrdinal;
    }

    public int getHierarchyInfoOrdinal(int ordinal) {
        return hierarchyInfoOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public StatusTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (StatusTypeAPI) typeAPI;
    }

}