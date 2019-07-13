package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RolloutDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutDelegate {

    private final Long rolloutId;
    private final Long movieId;
    private final String rolloutName;
    private final int rolloutNameOrdinal;
    private final String rolloutType;
    private final int rolloutTypeOrdinal;
    private final int phasesOrdinal;
    private RolloutTypeAPI typeAPI;

    public RolloutDelegateCachedImpl(RolloutTypeAPI typeAPI, int ordinal) {
        this.rolloutId = typeAPI.getRolloutIdBoxed(ordinal);
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.rolloutNameOrdinal = typeAPI.getRolloutNameOrdinal(ordinal);
        int rolloutNameTempOrdinal = rolloutNameOrdinal;
        this.rolloutName = rolloutNameTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(rolloutNameTempOrdinal);
        this.rolloutTypeOrdinal = typeAPI.getRolloutTypeOrdinal(ordinal);
        int rolloutTypeTempOrdinal = rolloutTypeOrdinal;
        this.rolloutType = rolloutTypeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(rolloutTypeTempOrdinal);
        this.phasesOrdinal = typeAPI.getPhasesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getRolloutId(int ordinal) {
        if(rolloutId == null)
            return Long.MIN_VALUE;
        return rolloutId.longValue();
    }

    public Long getRolloutIdBoxed(int ordinal) {
        return rolloutId;
    }

    public long getMovieId(int ordinal) {
        if(movieId == null)
            return Long.MIN_VALUE;
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public String getRolloutName(int ordinal) {
        return rolloutName;
    }

    public boolean isRolloutNameEqual(int ordinal, String testValue) {
        if(testValue == null)
            return rolloutName == null;
        return testValue.equals(rolloutName);
    }

    public int getRolloutNameOrdinal(int ordinal) {
        return rolloutNameOrdinal;
    }

    public String getRolloutType(int ordinal) {
        return rolloutType;
    }

    public boolean isRolloutTypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return rolloutType == null;
        return testValue.equals(rolloutType);
    }

    public int getRolloutTypeOrdinal(int ordinal) {
        return rolloutTypeOrdinal;
    }

    public int getPhasesOrdinal(int ordinal) {
        return phasesOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public RolloutTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RolloutTypeAPI) typeAPI;
    }

}