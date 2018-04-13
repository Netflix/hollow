package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class VideoStreamCropParamsHollow extends HollowObject {

    public VideoStreamCropParamsHollow(VideoStreamCropParamsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public int _getX() {
        return delegate().getX(ordinal);
    }

    public Integer _getXBoxed() {
        return delegate().getXBoxed(ordinal);
    }

    public int _getY() {
        return delegate().getY(ordinal);
    }

    public Integer _getYBoxed() {
        return delegate().getYBoxed(ordinal);
    }

    public int _getWidth() {
        return delegate().getWidth(ordinal);
    }

    public Integer _getWidthBoxed() {
        return delegate().getWidthBoxed(ordinal);
    }

    public int _getHeight() {
        return delegate().getHeight(ordinal);
    }

    public Integer _getHeightBoxed() {
        return delegate().getHeightBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoStreamCropParamsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoStreamCropParamsDelegate delegate() {
        return (VideoStreamCropParamsDelegate)delegate;
    }

}