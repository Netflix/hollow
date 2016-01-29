package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface TrailerTrailersDelegate extends HollowObjectDelegate {

    public int getThemesOrdinal(int ordinal);

    public long getSequenceNumber(int ordinal);

    public Long getSequenceNumberBoxed(int ordinal);

    public int getIdentifierOrdinal(int ordinal);

    public int getPostPlayOrdinal(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getSubTypeOrdinal(int ordinal);

    public int getAspectRatioOrdinal(int ordinal);

    public TrailerTrailersTypeAPI getTypeAPI();

}