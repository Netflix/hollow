package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class Stories_SynopsesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, Stories_SynopsesDelegate {

    private final Long movieId;
    private final int narrativeTextOrdinal;
    private final int hooksOrdinal;
   private Stories_SynopsesTypeAPI typeAPI;

    public Stories_SynopsesDelegateCachedImpl(Stories_SynopsesTypeAPI typeAPI, int ordinal) {
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

    public Stories_SynopsesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (Stories_SynopsesTypeAPI) typeAPI;
    }

}