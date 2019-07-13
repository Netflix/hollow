package com.netflix.vms.transformer.input.api.gen.rollout;

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

    public String getRolloutName(int ordinal) {
        ordinal = typeAPI.getRolloutNameOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isRolloutNameEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getRolloutNameOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getRolloutNameOrdinal(int ordinal) {
        return typeAPI.getRolloutNameOrdinal(ordinal);
    }

    public String getRolloutType(int ordinal) {
        ordinal = typeAPI.getRolloutTypeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isRolloutTypeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getRolloutTypeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
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