package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class ConsolidatedVideoRatingsDelegateLookupImpl extends HollowObjectAbstractDelegate implements ConsolidatedVideoRatingsDelegate {

    private final ConsolidatedVideoRatingsTypeAPI typeAPI;

    public ConsolidatedVideoRatingsDelegateLookupImpl(ConsolidatedVideoRatingsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getRatingsOrdinal(int ordinal) {
        return typeAPI.getRatingsOrdinal(ordinal);
    }

    public long getVideoId(int ordinal) {
        return typeAPI.getVideoId(ordinal);
    }

    public Long getVideoIdBoxed(int ordinal) {
        return typeAPI.getVideoIdBoxed(ordinal);
    }

    public ConsolidatedVideoRatingsTypeAPI getTypeAPI() {
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