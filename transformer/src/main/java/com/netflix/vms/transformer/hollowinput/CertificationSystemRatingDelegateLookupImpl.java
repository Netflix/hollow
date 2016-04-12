package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class CertificationSystemRatingDelegateLookupImpl extends HollowObjectAbstractDelegate implements CertificationSystemRatingDelegate {

    private final CertificationSystemRatingTypeAPI typeAPI;

    public CertificationSystemRatingDelegateLookupImpl(CertificationSystemRatingTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getRatingCodeOrdinal(int ordinal) {
        return typeAPI.getRatingCodeOrdinal(ordinal);
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

    public CertificationSystemRatingTypeAPI getTypeAPI() {
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