package com.netflix.vms.transformer.input.api.gen.mclEarliestDate;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface FeedMovieCountryLanguagesDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getMovieIdOrdinal(int ordinal);

    public String getCountryCode(int ordinal);

    public boolean isCountryCodeEqual(int ordinal, String testValue);

    public int getCountryCodeOrdinal(int ordinal);

    public int getLanguageToEarliestWindowStartDateMapOrdinal(int ordinal);

    public FeedMovieCountryLanguagesTypeAPI getTypeAPI();

}