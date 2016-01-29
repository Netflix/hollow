package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface DeployablePackagesDelegate extends HollowObjectDelegate {

    public int getCountryCodesOrdinal(int ordinal);

    public long getPackageId(int ordinal);

    public Long getPackageIdBoxed(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public DeployablePackagesTypeAPI getTypeAPI();

}