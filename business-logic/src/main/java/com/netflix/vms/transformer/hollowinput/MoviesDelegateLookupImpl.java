package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class MoviesDelegateLookupImpl extends HollowObjectAbstractDelegate implements MoviesDelegate {

    private final MoviesTypeAPI typeAPI;

    public MoviesDelegateLookupImpl(MoviesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        return typeAPI.getMovieId(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        return typeAPI.getMovieIdBoxed(ordinal);
    }

    public int getShortDisplayNameOrdinal(int ordinal) {
        return typeAPI.getShortDisplayNameOrdinal(ordinal);
    }

    public int getSiteSynopsisOrdinal(int ordinal) {
        return typeAPI.getSiteSynopsisOrdinal(ordinal);
    }

    public int getOriginalTitleOrdinal(int ordinal) {
        return typeAPI.getOriginalTitleOrdinal(ordinal);
    }

    public int getDisplayNameOrdinal(int ordinal) {
        return typeAPI.getDisplayNameOrdinal(ordinal);
    }

    public int getAkaOrdinal(int ordinal) {
        return typeAPI.getAkaOrdinal(ordinal);
    }

    public int getTransliteratedOrdinal(int ordinal) {
        return typeAPI.getTransliteratedOrdinal(ordinal);
    }

    public int getTvSynopsisOrdinal(int ordinal) {
        return typeAPI.getTvSynopsisOrdinal(ordinal);
    }

    public MoviesTypeAPI getTypeAPI() {
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