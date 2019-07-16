package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface MovieCountriesDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getMovieIdOrdinal(int ordinal);

    public int getCountriesOrdinal(int ordinal);

    public int getLastUpdatedCountriesOrdinal(int ordinal);

    public long getDateCreated(int ordinal);

    public Long getDateCreatedBoxed(int ordinal);

    public int getDateCreatedOrdinal(int ordinal);

    public long getLastUpdated(int ordinal);

    public Long getLastUpdatedBoxed(int ordinal);

    public int getLastUpdatedOrdinal(int ordinal);

    public String getCreatedBy(int ordinal);

    public boolean isCreatedByEqual(int ordinal, String testValue);

    public int getCreatedByOrdinal(int ordinal);

    public String getUpdatedBy(int ordinal);

    public boolean isUpdatedByEqual(int ordinal, String testValue);

    public int getUpdatedByOrdinal(int ordinal);

    public MovieCountriesTypeAPI getTypeAPI();

}