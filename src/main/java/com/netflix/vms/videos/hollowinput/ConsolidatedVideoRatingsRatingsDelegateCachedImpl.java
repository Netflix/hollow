package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class ConsolidatedVideoRatingsRatingsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ConsolidatedVideoRatingsRatingsDelegate {

    private final int countryRatingsOrdinal;
    private final int countryListOrdinal;
   private ConsolidatedVideoRatingsRatingsTypeAPI typeAPI;

    public ConsolidatedVideoRatingsRatingsDelegateCachedImpl(ConsolidatedVideoRatingsRatingsTypeAPI typeAPI, int ordinal) {
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

    public ConsolidatedVideoRatingsRatingsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ConsolidatedVideoRatingsRatingsTypeAPI) typeAPI;
    }

}