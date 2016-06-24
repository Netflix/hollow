package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VideoRightsDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getCountryCodeOrdinal(int ordinal);

    public int getRightsOrdinal(int ordinal);

    public int getFlagsOrdinal(int ordinal);

    public VideoRightsTypeAPI getTypeAPI();

}