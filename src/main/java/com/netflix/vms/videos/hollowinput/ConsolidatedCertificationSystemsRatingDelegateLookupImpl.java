package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedCertificationSystemsRatingDelegateLookupImpl extends HollowObjectAbstractDelegate implements ConsolidatedCertificationSystemsRatingDelegate {

    private final ConsolidatedCertificationSystemsRatingTypeAPI typeAPI;

    public ConsolidatedCertificationSystemsRatingDelegateLookupImpl(ConsolidatedCertificationSystemsRatingTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getRatingCodeOrdinal(int ordinal) {
        return typeAPI.getRatingCodeOrdinal(ordinal);
    }

    public int getRatingCodesOrdinal(int ordinal) {
        return typeAPI.getRatingCodesOrdinal(ordinal);
    }

    public long getRatingId(int ordinal) {
        return typeAPI.getRatingId(ordinal);
    }

    public Long getRatingIdBoxed(int ordinal) {
        return typeAPI.getRatingIdBoxed(ordinal);
    }

    public long getMaturityLevel(int ordinal) {
        return typeAPI.getMaturityLevel(ordinal);
    }

    public Long getMaturityLevelBoxed(int ordinal) {
        return typeAPI.getMaturityLevelBoxed(ordinal);
    }

    public int getDescriptionsOrdinal(int ordinal) {
        return typeAPI.getDescriptionsOrdinal(ordinal);
    }

    public ConsolidatedCertificationSystemsRatingTypeAPI getTypeAPI() {
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