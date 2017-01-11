package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StoriesSynopsesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, StoriesSynopsesDelegate {

    private final Long movieId;
    private final int narrativeTextOrdinal;
    private final int hooksOrdinal;
   private StoriesSynopsesTypeAPI typeAPI;

    public StoriesSynopsesDelegateCachedImpl(StoriesSynopsesTypeAPI typeAPI, int ordinal) {
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.narrativeTextOrdinal = typeAPI.getNarrativeTextOrdinal(ordinal);
        this.hooksOrdinal = typeAPI.getHooksOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public int getNarrativeTextOrdinal(int ordinal) {
        return narrativeTextOrdinal;
    }

    public int getHooksOrdinal(int ordinal) {
        return hooksOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public StoriesSynopsesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (StoriesSynopsesTypeAPI) typeAPI;
    }

}