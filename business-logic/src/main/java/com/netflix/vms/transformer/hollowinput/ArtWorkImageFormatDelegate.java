package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface ArtWorkImageFormatDelegate extends HollowObjectDelegate {

    public int getFormatOrdinal(int ordinal);

    public long getWidth(int ordinal);

    public Long getWidthBoxed(int ordinal);

    public long getHeight(int ordinal);

    public Long getHeightBoxed(int ordinal);

    public ArtWorkImageFormatTypeAPI getTypeAPI();

}