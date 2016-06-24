package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface StreamDeploymentInfoDelegate extends HollowObjectDelegate {

    public int getCacheDeployedCountriesOrdinal(int ordinal);

    public int getCdnDeploymentsOrdinal(int ordinal);

    public StreamDeploymentInfoTypeAPI getTypeAPI();

}