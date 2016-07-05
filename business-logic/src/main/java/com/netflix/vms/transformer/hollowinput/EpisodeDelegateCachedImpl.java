package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class EpisodeDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, EpisodeDelegate {

    private final Long sequenceNumber;
    private final Long movieId;
   private EpisodeTypeAPI typeAPI;

    public EpisodeDelegateCachedImpl(EpisodeTypeAPI typeAPI, int ordinal) {
        this.sequenceNumber = typeAPI.getSequenceNumberBoxed(ordinal);
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
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

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public EpisodeTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (EpisodeTypeAPI) typeAPI;
    }

}