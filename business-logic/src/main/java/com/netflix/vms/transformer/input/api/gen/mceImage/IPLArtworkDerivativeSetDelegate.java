package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface IPLArtworkDerivativeSetDelegate extends HollowObjectDelegate {

    public String getDerivativeSetId(int ordinal);

    public boolean isDerivativeSetIdEqual(int ordinal, String testValue);

    public int getDerivativeSetIdOrdinal(int ordinal);

    public int getDerivativesGroupBySourceOrdinal(int ordinal);

    public IPLArtworkDerivativeSetTypeAPI getTypeAPI();

}