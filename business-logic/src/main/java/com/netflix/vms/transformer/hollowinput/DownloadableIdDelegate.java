package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface DownloadableIdDelegate extends HollowObjectDelegate {

    public long getValue(int ordinal);

    public Long getValueBoxed(int ordinal);

    public DownloadableIdTypeAPI getTypeAPI();

}