package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class ConsolidatedVideoRatingDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ConsolidatedVideoRatingDelegate {

    private final int countryRatingsOrdinal;
    private final int countryListOrdinal;
   private ConsolidatedVideoRatingTypeAPI typeAPI;

    public ConsolidatedVideoRatingDelegateCachedImpl(ConsolidatedVideoRatingTypeAPI typeAPI, int ordinal) {
        this.countryRatingsOrdinal = typeAPI.getCountryRatingsOrdinal(ordinal);
        this.countryListOrdinal = typeAPI.getCountryListOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getCountryRatingsOrdinal(int ordinal) {
        return countryRatingsOrdinal;
    }

    public int getCountryListOrdinal(int ordinal) {
        return countryListOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public ConsolidatedVideoRatingTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ConsolidatedVideoRatingTypeAPI) typeAPI;
    }

}