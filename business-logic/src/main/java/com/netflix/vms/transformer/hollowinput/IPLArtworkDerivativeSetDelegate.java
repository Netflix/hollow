package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface IPLArtworkDerivativeSetDelegate extends HollowObjectDelegate {

    public int getDerivativeSetIdOrdinal(int ordinal);

    public int getDerivativesGroupBySourceOrdinal(int ordinal);

    public IPLArtworkDerivativeSetTypeAPI getTypeAPI();

}