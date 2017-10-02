package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class StreamDimensionsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, StreamDimensionsDelegate {

    private final Integer widthInPixels;
    private final Integer heightInPixels;
    private final Integer pixelAspectRatioWidth;
    private final Integer pixelAspectRatioHeight;
    private final Integer targetWidthInPixels;
    private final Integer targetHeightInPixels;
    private StreamDimensionsTypeAPI typeAPI;

    public StreamDimensionsDelegateCachedImpl(StreamDimensionsTypeAPI typeAPI, int ordinal) {
        this.widthInPixels = typeAPI.getWidthInPixelsBoxed(ordinal);
        this.heightInPixels = typeAPI.getHeightInPixelsBoxed(ordinal);
        this.pixelAspectRatioWidth = typeAPI.getPixelAspectRatioWidthBoxed(ordinal);
        this.pixelAspectRatioHeight = typeAPI.getPixelAspectRatioHeightBoxed(ordinal);
        this.targetWidthInPixels = typeAPI.getTargetWidthInPixelsBoxed(ordinal);
        this.targetHeightInPixels = typeAPI.getTargetHeightInPixelsBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getWidthInPixels(int ordinal) {
        if(widthInPixels == null)
            return Integer.MIN_VALUE;
        return widthInPixels.intValue();
    }

    public Integer getWidthInPixelsBoxed(int ordinal) {
        return widthInPixels;
    }

    public int getHeightInPixels(int ordinal) {
        if(heightInPixels == null)
            return Integer.MIN_VALUE;
        return heightInPixels.intValue();
    }

    public Integer getHeightInPixelsBoxed(int ordinal) {
        return heightInPixels;
    }

    public int getPixelAspectRatioWidth(int ordinal) {
        if(pixelAspectRatioWidth == null)
            return Integer.MIN_VALUE;
        return pixelAspectRatioWidth.intValue();
    }

    public Integer getPixelAspectRatioWidthBoxed(int ordinal) {
        return pixelAspectRatioWidth;
    }

    public int getPixelAspectRatioHeight(int ordinal) {
        if(pixelAspectRatioHeight == null)
            return Integer.MIN_VALUE;
        return pixelAspectRatioHeight.intValue();
    }

    public Integer getPixelAspectRatioHeightBoxed(int ordinal) {
        return pixelAspectRatioHeight;
    }

    public int getTargetWidthInPixels(int ordinal) {
        if(targetWidthInPixels == null)
            return Integer.MIN_VALUE;
        return targetWidthInPixels.intValue();
    }

    public Integer getTargetWidthInPixelsBoxed(int ordinal) {
        return targetWidthInPixels;
    }

    public int getTargetHeightInPixels(int ordinal) {
        if(targetHeightInPixels == null)
            return Integer.MIN_VALUE;
        return targetHeightInPixels.intValue();
    }

    public Integer getTargetHeightInPixelsBoxed(int ordinal) {
        return targetHeightInPixels;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public StreamDimensionsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (StreamDimensionsTypeAPI) typeAPI;
    }

}