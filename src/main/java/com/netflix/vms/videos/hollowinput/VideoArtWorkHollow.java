package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoArtWorkHollow extends HollowObject {

    public VideoArtWorkHollow(VideoArtWorkDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getImageFormat() {
        int refOrdinal = delegate().getImageFormatOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VideoArtWorkArrayOfRecipesHollow _getRecipes() {
        int refOrdinal = delegate().getRecipesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoArtWorkArrayOfRecipesHollow(refOrdinal);
    }

    public long _getImageId() {
        return delegate().getImageId(ordinal);
    }

    public Long _getImageIdBoxed() {
        return delegate().getImageIdBoxed(ordinal);
    }

    public long _getSeqNum() {
        return delegate().getSeqNum(ordinal);
    }

    public Long _getSeqNumBoxed() {
        return delegate().getSeqNumBoxed(ordinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public VideoArtWorkArrayOfExtensionsHollow _getExtensions() {
        int refOrdinal = delegate().getExtensionsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoArtWorkArrayOfExtensionsHollow(refOrdinal);
    }

    public VideoArtWorkArrayOfLocalesHollow _getLocales() {
        int refOrdinal = delegate().getLocalesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoArtWorkArrayOfLocalesHollow(refOrdinal);
    }

    public long _getImageTypeId() {
        return delegate().getImageTypeId(ordinal);
    }

    public Long _getImageTypeIdBoxed() {
        return delegate().getImageTypeIdBoxed(ordinal);
    }

    public long _getOrdinalPriority() {
        return delegate().getOrdinalPriority(ordinal);
    }

    public Long _getOrdinalPriorityBoxed() {
        return delegate().getOrdinalPriorityBoxed(ordinal);
    }

    public VideoArtWorkArrayOfAttributesHollow _getAttributes() {
        int refOrdinal = delegate().getAttributesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoArtWorkArrayOfAttributesHollow(refOrdinal);
    }

    public StringHollow _getImageType() {
        int refOrdinal = delegate().getImageTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VideoArtWorkSourceAttributesHollow _getSourceAttributes() {
        int refOrdinal = delegate().getSourceAttributesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoArtWorkSourceAttributesHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoArtWorkTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoArtWorkDelegate delegate() {
        return (VideoArtWorkDelegate)delegate;
    }

}