package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class MovieTitleDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, MovieTitleDelegate {

    private final Long movieId;
    private final int movieIdOrdinal;
    private final String type;
    private final int typeOrdinal;
    private MovieTitleTypeAPI typeAPI;

    public MovieTitleDelegateCachedImpl(MovieTitleTypeAPI typeAPI, int ordinal) {
        this.movieIdOrdinal = typeAPI.getMovieIdOrdinal(ordinal);
        int movieIdTempOrdinal = movieIdOrdinal;
        this.movieId = movieIdTempOrdinal == -1 ? null : typeAPI.getAPI().getMovieIdTypeAPI().getValue(movieIdTempOrdinal);
        this.typeOrdinal = typeAPI.getTypeOrdinal(ordinal);
        int typeTempOrdinal = typeOrdinal;
        this.type = typeTempOrdinal == -1 ? null : typeAPI.getAPI().getMovieTitleTypeTypeAPI().get_name(typeTempOrdinal);
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

    public int getMovieIdOrdinal(int ordinal) {
        return movieIdOrdinal;
    }

    public String getType(int ordinal) {
        return type;
    }

    public boolean isTypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return type == null;
        return testValue.equals(type);
    }

    public int getTypeOrdinal(int ordinal) {
        return typeOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public MovieTitleTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (MovieTitleTypeAPI) typeAPI;
    }

}