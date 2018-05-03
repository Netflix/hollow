package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class MovieCharacterPersonDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, MovieCharacterPersonDelegate {

    private final Long movieId;
    private final int charactersOrdinal;
    private MovieCharacterPersonTypeAPI typeAPI;

    public MovieCharacterPersonDelegateCachedImpl(MovieCharacterPersonTypeAPI typeAPI, int ordinal) {
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.charactersOrdinal = typeAPI.getCharactersOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        if(movieId == null)
            return Long.MIN_VALUE;
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public int getCharactersOrdinal(int ordinal) {
        return charactersOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public MovieCharacterPersonTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (MovieCharacterPersonTypeAPI) typeAPI;
    }

}