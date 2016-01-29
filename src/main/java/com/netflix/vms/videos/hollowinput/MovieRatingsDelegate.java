package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface MovieRatingsDelegate extends HollowObjectDelegate {

    public int getRatingReasonOrdinal(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getMediaOrdinal(int ordinal);

    public long getCertificationTypeId(int ordinal);

    public Long getCertificationTypeIdBoxed(int ordinal);

    public MovieRatingsTypeAPI getTypeAPI();

}