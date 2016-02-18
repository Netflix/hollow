package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface IndividualTrailerDelegate extends HollowObjectDelegate {

    public int getIdentifierOrdinal(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public long getSequenceNumber(int ordinal);

    public Long getSequenceNumberBoxed(int ordinal);

    public int getPostPlayOrdinal(int ordinal);

    public int getSubTypeOrdinal(int ordinal);

    public int getAspectRatioOrdinal(int ordinal);

    public int getThemesOrdinal(int ordinal);

    public IndividualTrailerTypeAPI getTypeAPI();

}