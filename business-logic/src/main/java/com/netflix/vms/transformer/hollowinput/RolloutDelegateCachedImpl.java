package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class RolloutDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutDelegate {

    private final Long rolloutId;
    private final Long movieId;
    private final int rolloutNameOrdinal;
    private final int rolloutTypeOrdinal;
    private final int phasesOrdinal;
    private RolloutTypeAPI typeAPI;

    public RolloutDelegateCachedImpl(RolloutTypeAPI typeAPI, int ordinal) {
        this.rolloutId = typeAPI.getRolloutIdBoxed(ordinal);
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.rolloutNameOrdinal = typeAPI.getRolloutNameOrdinal(ordinal);
        this.rolloutTypeOrdinal = typeAPI.getRolloutTypeOrdinal(ordinal);
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

    public int getRolloutNameOrdinal(int ordinal) {
        return rolloutNameOrdinal;
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