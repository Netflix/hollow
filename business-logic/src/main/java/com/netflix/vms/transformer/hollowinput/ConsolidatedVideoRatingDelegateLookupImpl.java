package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class ConsolidatedVideoRatingDelegateLookupImpl extends HollowObjectAbstractDelegate implements ConsolidatedVideoRatingDelegate {

    private final ConsolidatedVideoRatingTypeAPI typeAPI;

    public ConsolidatedVideoRatingDelegateLookupImpl(ConsolidatedVideoRatingTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getCountryRatingsOrdinal(int ordinal) {
        return typeAPI.getCountryRatingsOrdinal(ordinal);
    }

    public int getCountryListOrdinal(int ordinal) {
        return typeAPI.getCountryListOrdinal(ordinal);
    }

    public ConsolidatedVideoRatingTypeAPI getTypeAPI() {
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