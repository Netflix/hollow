package com.netflix.vms.transformer.input.api.gen.supplemental;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface SupplementalsDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getSupplementalsOrdinal(int ordinal);

    public SupplementalsTypeAPI getTypeAPI();

}