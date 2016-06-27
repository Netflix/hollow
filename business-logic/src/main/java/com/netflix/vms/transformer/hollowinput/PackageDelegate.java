package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface PackageDelegate extends HollowObjectDelegate {

    public long getPackageId(int ordinal);

    public Long getPackageIdBoxed(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getMomentsOrdinal(int ordinal);

    public int getDrmInfoOrdinal(int ordinal);

    public int getDownloadablesOrdinal(int ordinal);

    public int getDefaultS3PathComponentOrdinal(int ordinal);

    public PackageTypeAPI getTypeAPI();

}