package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class MoviesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, MoviesDelegate {

    private final Long movieId;
    private final int shortDisplayNameOrdinal;
    private final int siteSynopsisOrdinal;
    private final int originalTitleOrdinal;
    private final int displayNameOrdinal;
    private final int akaOrdinal;
    private final int transliteratedOrdinal;
    private final int tvSynopsisOrdinal;
   private MoviesTypeAPI typeAPI;

    public MoviesDelegateCachedImpl(MoviesTypeAPI typeAPI, int ordinal) {
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.shortDisplayNameOrdinal = typeAPI.getShortDisplayNameOrdinal(ordinal);
        this.siteSynopsisOrdinal = typeAPI.getSiteSynopsisOrdinal(ordinal);
        this.originalTitleOrdinal = typeAPI.getOriginalTitleOrdinal(ordinal);
        this.displayNameOrdinal = typeAPI.getDisplayNameOrdinal(ordinal);
        this.akaOrdinal = typeAPI.getAkaOrdinal(ordinal);
        this.transliteratedOrdinal = typeAPI.getTransliteratedOrdinal(ordinal);
        this.tvSynopsisOrdinal = typeAPI.getTvSynopsisOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public int getShortDisplayNameOrdinal(int ordinal) {
        return shortDisplayNameOrdinal;
    }

    public int getSiteSynopsisOrdinal(int ordinal) {
        return siteSynopsisOrdinal;
    }

    public int getOriginalTitleOrdinal(int ordinal) {
        return originalTitleOrdinal;
    }

    public int getDisplayNameOrdinal(int ordinal) {
        return displayNameOrdinal;
    }

    public int getAkaOrdinal(int ordinal) {
        return akaOrdinal;
    }

    public int getTransliteratedOrdinal(int ordinal) {
        return transliteratedOrdinal;
    }

    public int getTvSynopsisOrdinal(int ordinal) {
        return tvSynopsisOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public MoviesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (MoviesTypeAPI) typeAPI;
    }

}