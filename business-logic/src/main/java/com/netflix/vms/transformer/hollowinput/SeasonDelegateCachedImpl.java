package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class SeasonDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, SeasonDelegate {

    private final Long sequenceNumber;
    private final Long movieId;
    private final int episodesOrdinal;
   private SeasonTypeAPI typeAPI;

    public SeasonDelegateCachedImpl(SeasonTypeAPI typeAPI, int ordinal) {
        this.sequenceNumber = typeAPI.getSequenceNumberBoxed(ordinal);
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.episodesOrdinal = typeAPI.getEpisodesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getSequenceNumber(int ordinal) {
        return sequenceNumber.longValue();
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return sequenceNumber;
    }

    public long getMovieId(int ordinal) {
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public int getEpisodesOrdinal(int ordinal) {
        return episodesOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public SeasonTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (SeasonTypeAPI) typeAPI;
    }

}