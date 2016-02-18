package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class IndividualTrailerDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, IndividualTrailerDelegate {

    private final int identifierOrdinal;
    private final Long movieId;
    private final Long sequenceNumber;
    private final int postPlayOrdinal;
    private final int subTypeOrdinal;
    private final int aspectRatioOrdinal;
    private final int themesOrdinal;
   private IndividualTrailerTypeAPI typeAPI;

    public IndividualTrailerDelegateCachedImpl(IndividualTrailerTypeAPI typeAPI, int ordinal) {
        this.identifierOrdinal = typeAPI.getIdentifierOrdinal(ordinal);
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.sequenceNumber = typeAPI.getSequenceNumberBoxed(ordinal);
        this.postPlayOrdinal = typeAPI.getPostPlayOrdinal(ordinal);
        this.subTypeOrdinal = typeAPI.getSubTypeOrdinal(ordinal);
        this.aspectRatioOrdinal = typeAPI.getAspectRatioOrdinal(ordinal);
        this.themesOrdinal = typeAPI.getThemesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getIdentifierOrdinal(int ordinal) {
        return identifierOrdinal;
    }

    public long getMovieId(int ordinal) {
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public long getSequenceNumber(int ordinal) {
        return sequenceNumber.longValue();
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return sequenceNumber;
    }

    public int getPostPlayOrdinal(int ordinal) {
        return postPlayOrdinal;
    }

    public int getSubTypeOrdinal(int ordinal) {
        return subTypeOrdinal;
    }

    public int getAspectRatioOrdinal(int ordinal) {
        return aspectRatioOrdinal;
    }

    public int getThemesOrdinal(int ordinal) {
        return themesOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public IndividualTrailerTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (IndividualTrailerTypeAPI) typeAPI;
    }

}