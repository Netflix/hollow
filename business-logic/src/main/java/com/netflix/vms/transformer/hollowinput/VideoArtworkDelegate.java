package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VideoArtworkDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getSourceFileIdOrdinal(int ordinal);

    public long getSeqNum(int ordinal);

    public Long getSeqNumBoxed(int ordinal);

    public int getDerivativesOrdinal(int ordinal);

    public int getDerivativeSetOrdinal(int ordinal);

    public int getLocalesOrdinal(int ordinal);

    public int getAttributesOrdinal(int ordinal);

    public long getOrdinalPriority(int ordinal);

    public Long getOrdinalPriorityBoxed(int ordinal);

    public int getFileImageTypeOrdinal(int ordinal);

    public VideoArtworkTypeAPI getTypeAPI();

}