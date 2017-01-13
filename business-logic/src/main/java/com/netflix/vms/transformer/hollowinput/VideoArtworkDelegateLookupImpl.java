package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoArtworkDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoArtworkDelegate {

    private final VideoArtworkTypeAPI typeAPI;

    public VideoArtworkDelegateLookupImpl(VideoArtworkTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        return typeAPI.getMovieId(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        return typeAPI.getMovieIdBoxed(ordinal);
    }

    public int getSourceFileIdOrdinal(int ordinal) {
        return typeAPI.getSourceFileIdOrdinal(ordinal);
    }

    public long getSeqNum(int ordinal) {
        return typeAPI.getSeqNum(ordinal);
    }

    public Long getSeqNumBoxed(int ordinal) {
        return typeAPI.getSeqNumBoxed(ordinal);
    }

    public int getDerivativesOrdinal(int ordinal) {
        return typeAPI.getDerivativesOrdinal(ordinal);
    }

    public int getLocalesOrdinal(int ordinal) {
        return typeAPI.getLocalesOrdinal(ordinal);
    }

    public int getAttributesOrdinal(int ordinal) {
        return typeAPI.getAttributesOrdinal(ordinal);
    }

    public long getOrdinalPriority(int ordinal) {
        return typeAPI.getOrdinalPriority(ordinal);
    }

    public Long getOrdinalPriorityBoxed(int ordinal) {
        return typeAPI.getOrdinalPriorityBoxed(ordinal);
    }

    public int getFileImageTypeOrdinal(int ordinal) {
        return typeAPI.getFileImageTypeOrdinal(ordinal);
    }

    public int getPhaseTagsOrdinal(int ordinal) {
        return typeAPI.getPhaseTagsOrdinal(ordinal);
    }

    public boolean getIsSmoky(int ordinal) {
        return typeAPI.getIsSmoky(ordinal);
    }

    public Boolean getIsSmokyBoxed(int ordinal) {
        return typeAPI.getIsSmokyBoxed(ordinal);
    }

    public boolean getRolloutExclusive(int ordinal) {
        return typeAPI.getRolloutExclusive(ordinal);
    }

    public Boolean getRolloutExclusiveBoxed(int ordinal) {
        return typeAPI.getRolloutExclusiveBoxed(ordinal);
    }

    public VideoArtworkTypeAPI getTypeAPI() {
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