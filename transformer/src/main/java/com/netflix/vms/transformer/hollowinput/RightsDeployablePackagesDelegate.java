package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface RightsDeployablePackagesDelegate extends HollowObjectDelegate {

    public long getPackageId(int ordinal);

    public Long getPackageIdBoxed(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getCountryCodesOrdinal(int ordinal);

    public RightsDeployablePackagesTypeAPI getTypeAPI();

}