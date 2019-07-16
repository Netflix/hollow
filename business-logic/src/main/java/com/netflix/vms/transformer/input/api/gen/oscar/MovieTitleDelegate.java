package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface MovieTitleDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getMovieIdOrdinal(int ordinal);

    public String getType(int ordinal);

    public boolean isTypeEqual(int ordinal, String testValue);

    public int getTypeOrdinal(int ordinal);

    public MovieTitleTypeAPI getTypeAPI();

}