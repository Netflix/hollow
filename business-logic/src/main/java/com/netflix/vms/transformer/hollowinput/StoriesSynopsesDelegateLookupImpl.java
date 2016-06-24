package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class StoriesSynopsesDelegateLookupImpl extends HollowObjectAbstractDelegate implements StoriesSynopsesDelegate {

    private final StoriesSynopsesTypeAPI typeAPI;

    public StoriesSynopsesDelegateLookupImpl(StoriesSynopsesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        return typeAPI.getMovieId(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        return typeAPI.getMovieIdBoxed(ordinal);
    }

    public int getNarrativeTextOrdinal(int ordinal) {
        return typeAPI.getNarrativeTextOrdinal(ordinal);
    }

    public int getHooksOrdinal(int ordinal) {
        return typeAPI.getHooksOrdinal(ordinal);
    }

    public StoriesSynopsesTypeAPI getTypeAPI() {
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