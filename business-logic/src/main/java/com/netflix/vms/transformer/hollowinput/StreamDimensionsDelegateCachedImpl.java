package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

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
        return widthInPixels.intValue();
    }

    public Integer getWidthInPixelsBoxed(int ordinal) {
        return widthInPixels;
    }

    public int getHeightInPixels(int ordinal) {
        return heightInPixels.intValue();
    }

    public Integer getHeightInPixelsBoxed(int ordinal) {
        return heightInPixels;
    }

    public int getPixelAspectRatioWidth(int ordinal) {
        return pixelAspectRatioWidth.intValue();
    }

    public Integer getPixelAspectRatioWidthBoxed(int ordinal) {
        return pixelAspectRatioWidth;
    }

    public int getPixelAspectRatioHeight(int ordinal) {
        return pixelAspectRatioHeight.intValue();
    }

    public Integer getPixelAspectRatioHeightBoxed(int ordinal) {
        return pixelAspectRatioHeight;
    }

    public int getTargetWidthInPixels(int ordinal) {
        return targetWidthInPixels.intValue();
    }

    public Integer getTargetWidthInPixelsBoxed(int ordinal) {
        return targetWidthInPixels;
    }

    public int getTargetHeightInPixels(int ordinal) {
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