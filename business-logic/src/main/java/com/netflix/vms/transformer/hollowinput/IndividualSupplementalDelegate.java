package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface IndividualSupplementalDelegate extends HollowObjectDelegate {

    public int getIdentifierOrdinal(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public long getSequenceNumber(int ordinal);

    public Long getSequenceNumberBoxed(int ordinal);

    public int getPassthroughOrdinal(int ordinal);

    public IndividualSupplementalTypeAPI getTypeAPI();

}