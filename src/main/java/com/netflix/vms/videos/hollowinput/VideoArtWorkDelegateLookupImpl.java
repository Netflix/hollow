package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoArtWorkDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoArtWorkDelegate {

    private final VideoArtWorkTypeAPI typeAPI;

    public VideoArtWorkDelegateLookupImpl(VideoArtWorkTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getImageFormatOrdinal(int ordinal) {
        return typeAPI.getImageFormatOrdinal(ordinal);
    }

    public int getRecipesOrdinal(int ordinal) {
        return typeAPI.getRecipesOrdinal(ordinal);
    }

    public long getImageId(int ordinal) {
        return typeAPI.getImageId(ordinal);
    }

    public Long getImageIdBoxed(int ordinal) {
        return typeAPI.getImageIdBoxed(ordinal);
    }

    public long getSeqNum(int ordinal) {
        return typeAPI.getSeqNum(ordinal);
    }

    public Long getSeqNumBoxed(int ordinal) {
        return typeAPI.getSeqNumBoxed(ordinal);
    }

    public long getMovieId(int ordinal) {
        return typeAPI.getMovieId(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        return typeAPI.getMovieIdBoxed(ordinal);
    }

    public int getExtensionsOrdinal(int ordinal) {
        return typeAPI.getExtensionsOrdinal(ordinal);
    }

    public int getLocalesOrdinal(int ordinal) {
        return typeAPI.getLocalesOrdinal(ordinal);
    }

    public long getImageTypeId(int ordinal) {
        return typeAPI.getImageTypeId(ordinal);
    }

    public Long getImageTypeIdBoxed(int ordinal) {
        return typeAPI.getImageTypeIdBoxed(ordinal);
    }

    public long getOrdinalPriority(int ordinal) {
        return typeAPI.getOrdinalPriority(ordinal);
    }

    public Long getOrdinalPriorityBoxed(int ordinal) {
        return typeAPI.getOrdinalPriorityBoxed(ordinal);
    }

    public int getAttributesOrdinal(int ordinal) {
        return typeAPI.getAttributesOrdinal(ordinal);
    }

    public int getImageTypeOrdinal(int ordinal) {
        return typeAPI.getImageTypeOrdinal(ordinal);
    }

    public int getSourceAttributesOrdinal(int ordinal) {
        return typeAPI.getSourceAttributesOrdinal(ordinal);
    }

    public VideoArtWorkTypeAPI getTypeAPI() {
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