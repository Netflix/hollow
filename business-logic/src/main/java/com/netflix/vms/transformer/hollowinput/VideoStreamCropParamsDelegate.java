package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VideoStreamCropParamsDelegate extends HollowObjectDelegate {

    public int getX(int ordinal);

    public Integer getXBoxed(int ordinal);

    public int getY(int ordinal);

    public Integer getYBoxed(int ordinal);

    public int getWidth(int ordinal);

    public Integer getWidthBoxed(int ordinal);

    public int getHeight(int ordinal);

    public Integer getHeightBoxed(int ordinal);

    public VideoStreamCropParamsTypeAPI getTypeAPI();

}