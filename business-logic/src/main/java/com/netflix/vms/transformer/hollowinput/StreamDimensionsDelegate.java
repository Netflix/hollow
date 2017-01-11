package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface StreamDimensionsDelegate extends HollowObjectDelegate {

    public int getWidthInPixels(int ordinal);

    public Integer getWidthInPixelsBoxed(int ordinal);

    public int getHeightInPixels(int ordinal);

    public Integer getHeightInPixelsBoxed(int ordinal);

    public int getPixelAspectRatioWidth(int ordinal);

    public Integer getPixelAspectRatioWidthBoxed(int ordinal);

    public int getPixelAspectRatioHeight(int ordinal);

    public Integer getPixelAspectRatioHeightBoxed(int ordinal);

    public int getTargetWidthInPixels(int ordinal);

    public Integer getTargetWidthInPixelsBoxed(int ordinal);

    public int getTargetHeightInPixels(int ordinal);

    public Integer getTargetHeightInPixelsBoxed(int ordinal);

    public StreamDimensionsTypeAPI getTypeAPI();

}