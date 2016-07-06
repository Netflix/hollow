package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface StatusDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getCountryCodeOrdinal(int ordinal);

    public int getRightsOrdinal(int ordinal);

    public int getFlagsOrdinal(int ordinal);

    public StatusTypeAPI getTypeAPI();

}