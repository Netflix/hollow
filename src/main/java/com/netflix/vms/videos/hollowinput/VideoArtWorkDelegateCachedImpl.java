package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoArtWorkDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoArtWorkDelegate {

    private final int imageFormatOrdinal;
    private final int recipesOrdinal;
    private final Long imageId;
    private final Long seqNum;
    private final Long movieId;
    private final int extensionsOrdinal;
    private final int localesOrdinal;
    private final Long imageTypeId;
    private final Long ordinalPriority;
    private final int attributesOrdinal;
    private final int imageTypeOrdinal;
    private final int sourceAttributesOrdinal;
   private VideoArtWorkTypeAPI typeAPI;

    public VideoArtWorkDelegateCachedImpl(VideoArtWorkTypeAPI typeAPI, int ordinal) {
        this.imageFormatOrdinal = typeAPI.getImageFormatOrdinal(ordinal);
        this.recipesOrdinal = typeAPI.getRecipesOrdinal(ordinal);
        this.imageId = typeAPI.getImageIdBoxed(ordinal);
        this.seqNum = typeAPI.getSeqNumBoxed(ordinal);
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.extensionsOrdinal = typeAPI.getExtensionsOrdinal(ordinal);
        this.localesOrdinal = typeAPI.getLocalesOrdinal(ordinal);
        this.imageTypeId = typeAPI.getImageTypeIdBoxed(ordinal);
        this.ordinalPriority = typeAPI.getOrdinalPriorityBoxed(ordinal);
        this.attributesOrdinal = typeAPI.getAttributesOrdinal(ordinal);
        this.imageTypeOrdinal = typeAPI.getImageTypeOrdinal(ordinal);
        this.sourceAttributesOrdinal = typeAPI.getSourceAttributesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getImageFormatOrdinal(int ordinal) {
        return imageFormatOrdinal;
    }

    public int getRecipesOrdinal(int ordinal) {
        return recipesOrdinal;
    }

    public long getImageId(int ordinal) {
        return imageId.longValue();
    }

    public Long getImageIdBoxed(int ordinal) {
        return imageId;
    }

    public long getSeqNum(int ordinal) {
        return seqNum.longValue();
    }

    public Long getSeqNumBoxed(int ordinal) {
        return seqNum;
    }

    public long getMovieId(int ordinal) {
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public int getExtensionsOrdinal(int ordinal) {
        return extensionsOrdinal;
    }

    public int getLocalesOrdinal(int ordinal) {
        return localesOrdinal;
    }

    public long getImageTypeId(int ordinal) {
        return imageTypeId.longValue();
    }

    public Long getImageTypeIdBoxed(int ordinal) {
        return imageTypeId;
    }

    public long getOrdinalPriority(int ordinal) {
        return ordinalPriority.longValue();
    }

    public Long getOrdinalPriorityBoxed(int ordinal) {
        return ordinalPriority;
    }

    public int getAttributesOrdinal(int ordinal) {
        return attributesOrdinal;
    }

    public int getImageTypeOrdinal(int ordinal) {
        return imageTypeOrdinal;
    }

    public int getSourceAttributesOrdinal(int ordinal) {
        return sourceAttributesOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoArtWorkTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoArtWorkTypeAPI) typeAPI;
    }

}