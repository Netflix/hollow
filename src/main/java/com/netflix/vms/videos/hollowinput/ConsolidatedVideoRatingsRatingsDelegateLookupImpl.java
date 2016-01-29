package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedVideoRatingsRatingsDelegateLookupImpl extends HollowObjectAbstractDelegate implements ConsolidatedVideoRatingsRatingsDelegate {

    private final ConsolidatedVideoRatingsRatingsTypeAPI typeAPI;

    public ConsolidatedVideoRatingsRatingsDelegateLookupImpl(ConsolidatedVideoRatingsRatingsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getCountryRatingsOrdinal(int ordinal) {
        return typeAPI.getCountryRatingsOrdinal(ordinal);
    }

    public int getCountryListOrdinal(int ordinal) {
        return typeAPI.getCountryListOrdinal(ordinal);
    }

    public ConsolidatedVideoRatingsRatingsTypeAPI getTypeAPI() {
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