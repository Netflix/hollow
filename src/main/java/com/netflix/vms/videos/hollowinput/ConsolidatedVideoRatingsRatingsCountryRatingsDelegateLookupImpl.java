package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedVideoRatingsRatingsCountryRatingsDelegateLookupImpl extends HollowObjectAbstractDelegate implements ConsolidatedVideoRatingsRatingsCountryRatingsDelegate {

    private final ConsolidatedVideoRatingsRatingsCountryRatingsTypeAPI typeAPI;

    public ConsolidatedVideoRatingsRatingsCountryRatingsDelegateLookupImpl(ConsolidatedVideoRatingsRatingsCountryRatingsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getAdvisoriesOrdinal(int ordinal) {
        return typeAPI.getAdvisoriesOrdinal(ordinal);
    }

    public int getReasonsOrdinal(int ordinal) {
        return typeAPI.getReasonsOrdinal(ordinal);
    }

    public long getRatingId(int ordinal) {
        return typeAPI.getRatingId(ordinal);
    }

    public Long getRatingIdBoxed(int ordinal) {
        return typeAPI.getRatingIdBoxed(ordinal);
    }

    public long getCertificationSystemId(int ordinal) {
        return typeAPI.getCertificationSystemId(ordinal);
    }

    public Long getCertificationSystemIdBoxed(int ordinal) {
        return typeAPI.getCertificationSystemIdBoxed(ordinal);
    }

    public ConsolidatedVideoRatingsRatingsCountryRatingsTypeAPI getTypeAPI() {
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