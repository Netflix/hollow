package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface CacheDeploymentIntentDelegate extends HollowObjectDelegate {

    public long getStreamProfileId(int ordinal);

    public Long getStreamProfileIdBoxed(int ordinal);

    public int getIsoCountryCodeOrdinal(int ordinal);

    public long getBitrateKBPS(int ordinal);

    public Long getBitrateKBPSBoxed(int ordinal);

    public CacheDeploymentIntentTypeAPI getTypeAPI();

}