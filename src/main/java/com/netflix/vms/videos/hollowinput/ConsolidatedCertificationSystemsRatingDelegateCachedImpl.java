package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class ConsolidatedCertificationSystemsRatingDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ConsolidatedCertificationSystemsRatingDelegate {

    private final int ratingCodeOrdinal;
    private final int ratingCodesOrdinal;
    private final Long ratingId;
    private final Long maturityLevel;
    private final int descriptionsOrdinal;
   private ConsolidatedCertificationSystemsRatingTypeAPI typeAPI;

    public ConsolidatedCertificationSystemsRatingDelegateCachedImpl(ConsolidatedCertificationSystemsRatingTypeAPI typeAPI, int ordinal) {
        this.ratingCodeOrdinal = typeAPI.getRatingCodeOrdinal(ordinal);
        this.ratingCodesOrdinal = typeAPI.getRatingCodesOrdinal(ordinal);
        this.ratingId = typeAPI.getRatingIdBoxed(ordinal);
        this.maturityLevel = typeAPI.getMaturityLevelBoxed(ordinal);
        this.descriptionsOrdinal = typeAPI.getDescriptionsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getRatingCodeOrdinal(int ordinal) {
        return ratingCodeOrdinal;
    }

    public int getRatingCodesOrdinal(int ordinal) {
        return ratingCodesOrdinal;
    }

    public long getRatingId(int ordinal) {
        return ratingId.longValue();
    }

    public Long getRatingIdBoxed(int ordinal) {
        return ratingId;
    }

    public long getMaturityLevel(int ordinal) {
        return maturityLevel.longValue();
    }

    public Long getMaturityLevelBoxed(int ordinal) {
        return maturityLevel;
    }

    public int getDescriptionsOrdinal(int ordinal) {
        return descriptionsOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public ConsolidatedCertificationSystemsRatingTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ConsolidatedCertificationSystemsRatingTypeAPI) typeAPI;
    }

}