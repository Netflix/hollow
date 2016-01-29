package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class Stories_SynopsesDelegateLookupImpl extends HollowObjectAbstractDelegate implements Stories_SynopsesDelegate {

    private final Stories_SynopsesTypeAPI typeAPI;

    public Stories_SynopsesDelegateLookupImpl(Stories_SynopsesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getNarrativeTextOrdinal(int ordinal) {
        return typeAPI.getNarrativeTextOrdinal(ordinal);
    }

    public long getMovieId(int ordinal) {
        return typeAPI.getMovieId(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        return typeAPI.getMovieIdBoxed(ordinal);
    }

    public int getHooksOrdinal(int ordinal) {
        return typeAPI.getHooksOrdinal(ordinal);
    }

    public Stories_SynopsesTypeAPI getTypeAPI() {
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