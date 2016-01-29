package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface DeployablePackagesCountryCodesDelegate extends HollowObjectDelegate {

    public int getValueOrdinal(int ordinal);

    public DeployablePackagesCountryCodesTypeAPI getTypeAPI();

}