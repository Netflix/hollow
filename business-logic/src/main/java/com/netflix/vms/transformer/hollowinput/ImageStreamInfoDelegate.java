package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface ImageStreamInfoDelegate extends HollowObjectDelegate {

    public int getImageCount(int ordinal);

    public Integer getImageCountBoxed(int ordinal);

    public int getImageFormatOrdinal(int ordinal);

    public long getOffsetMillis(int ordinal);

    public Long getOffsetMillisBoxed(int ordinal);

    public ImageStreamInfoTypeAPI getTypeAPI();

}