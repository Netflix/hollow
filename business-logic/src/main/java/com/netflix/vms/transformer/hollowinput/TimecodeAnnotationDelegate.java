package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface TimecodeAnnotationDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public long getPackageId(int ordinal);

    public Long getPackageIdBoxed(int ordinal);

    public int getTimecodeAnnotationsOrdinal(int ordinal);

    public TimecodeAnnotationTypeAPI getTypeAPI();

}