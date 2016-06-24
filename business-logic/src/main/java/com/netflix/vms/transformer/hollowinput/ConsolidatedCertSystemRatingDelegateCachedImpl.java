package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class ConsolidatedCertSystemRatingDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ConsolidatedCertSystemRatingDelegate {

    private final Long ratingId;
    private final Long maturityLevel;
    private final int ratingCodeOrdinal;
    private final int ratingCodesOrdinal;
    private final int descriptionsOrdinal;
   private ConsolidatedCertSystemRatingTypeAPI typeAPI;

    public ConsolidatedCertSystemRatingDelegateCachedImpl(ConsolidatedCertSystemRatingTypeAPI typeAPI, int ordinal) {
        this.ratingId = typeAPI.getRatingIdBoxed(ordinal);
        this.maturityLevel = typeAPI.getMaturityLevelBoxed(ordinal);
        this.ratingCodeOrdinal = typeAPI.getRatingCodeOrdinal(ordinal);
        this.ratingCodesOrdinal = typeAPI.getRatingCodesOrdinal(ordinal);
        this.descriptionsOrdinal = typeAPI.getDescriptionsOrdinal(ordinal);
        this.typeAPI = typeAPI;
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

    public int getRatingCodeOrdinal(int ordinal) {
        return ratingCodeOrdinal;
    }

    public int getRatingCodesOrdinal(int ordinal) {
        return ratingCodesOrdinal;
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

    public ConsolidatedCertSystemRatingTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ConsolidatedCertSystemRatingTypeAPI) typeAPI;
    }

}