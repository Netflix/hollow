package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface FeedMovieCountryLanguagesDelegate extends HollowObjectDelegate {

    public int getMovieIdOrdinal(int ordinal);

    public int getCountryCodeOrdinal(int ordinal);

    public int getLanguageToEarliestWindowStartDateMapOrdinal(int ordinal);

    public FeedMovieCountryLanguagesTypeAPI getTypeAPI();

}