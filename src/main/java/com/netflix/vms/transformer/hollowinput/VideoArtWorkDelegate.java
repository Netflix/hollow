package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoArtWorkDelegate extends HollowObjectDelegate {

    public long getImageId(int ordinal);

    public Long getImageIdBoxed(int ordinal);

    public int getImageFormatOrdinal(int ordinal);

    public long getSeqNum(int ordinal);

    public Long getSeqNumBoxed(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public long getImageTypeId(int ordinal);

    public Long getImageTypeIdBoxed(int ordinal);

    public long getOrdinalPriority(int ordinal);

    public Long getOrdinalPriorityBoxed(int ordinal);

    public int getImageTypeOrdinal(int ordinal);

    public int getRecipesOrdinal(int ordinal);

    public int getExtensionsOrdinal(int ordinal);

    public int getLocalesOrdinal(int ordinal);

    public int getAttributesOrdinal(int ordinal);

    public int getSourceAttributesOrdinal(int ordinal);

    public VideoArtWorkTypeAPI getTypeAPI();

}