package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface DealCountryGroupDelegate extends HollowObjectDelegate {

    public long getDealId(int ordinal);

    public Long getDealIdBoxed(int ordinal);

    public int getDealIdOrdinal(int ordinal);

    public int getCountryWindowOrdinal(int ordinal);

    public DealCountryGroupTypeAPI getTypeAPI();

}