package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhaseTrailerDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutPhaseTrailerDelegate {

    private final RolloutPhaseTrailerTypeAPI typeAPI;

    public RolloutPhaseTrailerDelegateLookupImpl(RolloutPhaseTrailerTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getSequenceNumber(int ordinal) {
        return typeAPI.getSequenceNumber(ordinal);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return typeAPI.getSequenceNumberBoxed(ordinal);
    }

    public long getTrailerMovieId(int ordinal) {
        return typeAPI.getTrailerMovieId(ordinal);
    }

    public Long getTrailerMovieIdBoxed(int ordinal) {
        return typeAPI.getTrailerMovieIdBoxed(ordinal);
    }

    public int getSupplementalInfoOrdinal(int ordinal) {
        return typeAPI.getSupplementalInfoOrdinal(ordinal);
    }

    public RolloutPhaseTrailerTypeAPI getTypeAPI() {
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