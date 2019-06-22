package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface StatusDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public String getCountryCode(int ordinal);

    public boolean isCountryCodeEqual(int ordinal, String testValue);

    public int getCountryCodeOrdinal(int ordinal);

    public int getRightsOrdinal(int ordinal);

    public int getFlagsOrdinal(int ordinal);

    public int getAvailableAssetsOrdinal(int ordinal);

    public int getHierarchyInfoOrdinal(int ordinal);

    public StatusTypeAPI getTypeAPI();

}