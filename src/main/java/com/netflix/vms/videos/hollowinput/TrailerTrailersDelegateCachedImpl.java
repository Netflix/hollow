package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class TrailerTrailersDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, TrailerTrailersDelegate {

    private final int themesOrdinal;
    private final Long sequenceNumber;
    private final int identifierOrdinal;
    private final int postPlayOrdinal;
    private final Long movieId;
    private final int subTypeOrdinal;
    private final int aspectRatioOrdinal;
   private TrailerTrailersTypeAPI typeAPI;

    public TrailerTrailersDelegateCachedImpl(TrailerTrailersTypeAPI typeAPI, int ordinal) {
        this.themesOrdinal = typeAPI.getThemesOrdinal(ordinal);
        this.sequenceNumber = typeAPI.getSequenceNumberBoxed(ordinal);
        this.identifierOrdinal = typeAPI.getIdentifierOrdinal(ordinal);
        this.postPlayOrdinal = typeAPI.getPostPlayOrdinal(ordinal);
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.subTypeOrdinal = typeAPI.getSubTypeOrdinal(ordinal);
        this.aspectRatioOrdinal = typeAPI.getAspectRatioOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getThemesOrdinal(int ordinal) {
        return themesOrdinal;
    }

    public long getSequenceNumber(int ordinal) {
        return sequenceNumber.longValue();
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return sequenceNumber;
    }

    public int getIdentifierOrdinal(int ordinal) {
        return identifierOrdinal;
    }

    public int getPostPlayOrdinal(int ordinal) {
        return postPlayOrdinal;
    }

    public long getMovieId(int ordinal) {
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public int getSubTypeOrdinal(int ordinal) {
        return subTypeOrdinal;
    }

    public int getAspectRatioOrdinal(int ordinal) {
        return aspectRatioOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public TrailerTrailersTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (TrailerTrailersTypeAPI) typeAPI;
    }

}