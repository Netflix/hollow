package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class VideoArtworkDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoArtworkDelegate {

    private final Long movieId;
    private final int sourceFileIdOrdinal;
    private final Long seqNum;
    private final int derivativesOrdinal;
    private final int localesOrdinal;
    private final int attributesOrdinal;
    private final Long ordinalPriority;
    private final int fileImageTypeOrdinal;
    private final int phaseTagsOrdinal;
    private final Boolean isSmoky;
    private final Boolean rolloutExclusive;
   private VideoArtworkTypeAPI typeAPI;

    public VideoArtworkDelegateCachedImpl(VideoArtworkTypeAPI typeAPI, int ordinal) {
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.sourceFileIdOrdinal = typeAPI.getSourceFileIdOrdinal(ordinal);
        this.seqNum = typeAPI.getSeqNumBoxed(ordinal);
        this.derivativesOrdinal = typeAPI.getDerivativesOrdinal(ordinal);
        this.localesOrdinal = typeAPI.getLocalesOrdinal(ordinal);
        this.attributesOrdinal = typeAPI.getAttributesOrdinal(ordinal);
        this.ordinalPriority = typeAPI.getOrdinalPriorityBoxed(ordinal);
        this.fileImageTypeOrdinal = typeAPI.getFileImageTypeOrdinal(ordinal);
        this.phaseTagsOrdinal = typeAPI.getPhaseTagsOrdinal(ordinal);
        this.isSmoky = typeAPI.getIsSmokyBoxed(ordinal);
        this.rolloutExclusive = typeAPI.getRolloutExclusiveBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public int getSourceFileIdOrdinal(int ordinal) {
        return sourceFileIdOrdinal;
    }

    public long getSeqNum(int ordinal) {
        return seqNum.longValue();
    }

    public Long getSeqNumBoxed(int ordinal) {
        return seqNum;
    }

    public int getDerivativesOrdinal(int ordinal) {
        return derivativesOrdinal;
    }

    public int getLocalesOrdinal(int ordinal) {
        return localesOrdinal;
    }

    public int getAttributesOrdinal(int ordinal) {
        return attributesOrdinal;
    }

    public long getOrdinalPriority(int ordinal) {
        return ordinalPriority.longValue();
    }

    public Long getOrdinalPriorityBoxed(int ordinal) {
        return ordinalPriority;
    }

    public int getFileImageTypeOrdinal(int ordinal) {
        return fileImageTypeOrdinal;
    }

    public int getPhaseTagsOrdinal(int ordinal) {
        return phaseTagsOrdinal;
    }

    public boolean getIsSmoky(int ordinal) {
        return isSmoky.booleanValue();
    }

    public Boolean getIsSmokyBoxed(int ordinal) {
        return isSmoky;
    }

    public boolean getRolloutExclusive(int ordinal) {
        return rolloutExclusive.booleanValue();
    }

    public Boolean getRolloutExclusiveBoxed(int ordinal) {
        return rolloutExclusive;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoArtworkTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoArtworkTypeAPI) typeAPI;
    }

}