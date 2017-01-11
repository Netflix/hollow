package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RolloutDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutDelegate {

    private final RolloutTypeAPI typeAPI;

    public RolloutDelegateLookupImpl(RolloutTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getRolloutId(int ordinal) {
        return typeAPI.getRolloutId(ordinal);
    }

    public Long getRolloutIdBoxed(int ordinal) {
        return typeAPI.getRolloutIdBoxed(ordinal);
    }

    public long getMovieId(int ordinal) {
        return typeAPI.getMovieId(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        return typeAPI.getMovieIdBoxed(ordinal);
    }

    public int getRolloutNameOrdinal(int ordinal) {
        return typeAPI.getRolloutNameOrdinal(ordinal);
    }

    public int getRolloutTypeOrdinal(int ordinal) {
        return typeAPI.getRolloutTypeOrdinal(ordinal);
    }

    public int getPhasesOrdinal(int ordinal) {
        return typeAPI.getPhasesOrdinal(ordinal);
    }

    public RolloutTypeAPI getTypeAPI() {
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