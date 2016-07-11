package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class ConsolidatedVideoCountryRatingDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ConsolidatedVideoCountryRatingDelegate {

    private final int advisoriesOrdinal;
    private final int reasonsOrdinal;
    private final Long ratingId;
    private final Long certificationSystemId;
   private ConsolidatedVideoCountryRatingTypeAPI typeAPI;

    public ConsolidatedVideoCountryRatingDelegateCachedImpl(ConsolidatedVideoCountryRatingTypeAPI typeAPI, int ordinal) {
        this.advisoriesOrdinal = typeAPI.getAdvisoriesOrdinal(ordinal);
        this.reasonsOrdinal = typeAPI.getReasonsOrdinal(ordinal);
        this.ratingId = typeAPI.getRatingIdBoxed(ordinal);
        this.certificationSystemId = typeAPI.getCertificationSystemIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getAdvisoriesOrdinal(int ordinal) {
        return advisoriesOrdinal;
    }

    public int getReasonsOrdinal(int ordinal) {
        return reasonsOrdinal;
    }

    public long getRatingId(int ordinal) {
        return ratingId.longValue();
    }

    public Long getRatingIdBoxed(int ordinal) {
        return ratingId;
    }

    public long getCertificationSystemId(int ordinal) {
        return certificationSystemId.longValue();
    }

    public Long getCertificationSystemIdBoxed(int ordinal) {
        return certificationSystemId;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public ConsolidatedVideoCountryRatingTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ConsolidatedVideoCountryRatingTypeAPI) typeAPI;
    }

}