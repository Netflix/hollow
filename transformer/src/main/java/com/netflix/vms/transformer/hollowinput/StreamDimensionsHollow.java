package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class StreamDimensionsHollow extends HollowObject {

    public StreamDimensionsHollow(StreamDimensionsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public int _getWidthInPixels() {
        return delegate().getWidthInPixels(ordinal);
    }

    public Integer _getWidthInPixelsBoxed() {
        return delegate().getWidthInPixelsBoxed(ordinal);
    }

    public int _getHeightInPixels() {
        return delegate().getHeightInPixels(ordinal);
    }

    public Integer _getHeightInPixelsBoxed() {
        return delegate().getHeightInPixelsBoxed(ordinal);
    }

    public int _getPixelAspectRatioWidth() {
        return delegate().getPixelAspectRatioWidth(ordinal);
    }

    public Integer _getPixelAspectRatioWidthBoxed() {
        return delegate().getPixelAspectRatioWidthBoxed(ordinal);
    }

    public int _getPixelAspectRatioHeight() {
        return delegate().getPixelAspectRatioHeight(ordinal);
    }

    public Integer _getPixelAspectRatioHeightBoxed() {
        return delegate().getPixelAspectRatioHeightBoxed(ordinal);
    }

    public int _getTargetWidthInPixels() {
        return delegate().getTargetWidthInPixels(ordinal);
    }

    public Integer _getTargetWidthInPixelsBoxed() {
        return delegate().getTargetWidthInPixelsBoxed(ordinal);
    }

    public int _getTargetHeightInPixels() {
        return delegate().getTargetHeightInPixels(ordinal);
    }

    public Integer _getTargetHeightInPixelsBoxed() {
        return delegate().getTargetHeightInPixelsBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public StreamDimensionsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StreamDimensionsDelegate delegate() {
        return (StreamDimensionsDelegate)delegate;
    }

}