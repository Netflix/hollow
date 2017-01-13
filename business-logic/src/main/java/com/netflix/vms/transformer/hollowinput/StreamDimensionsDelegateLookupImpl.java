package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StreamDimensionsDelegateLookupImpl extends HollowObjectAbstractDelegate implements StreamDimensionsDelegate {

    private final StreamDimensionsTypeAPI typeAPI;

    public StreamDimensionsDelegateLookupImpl(StreamDimensionsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getWidthInPixels(int ordinal) {
        return typeAPI.getWidthInPixels(ordinal);
    }

    public Integer getWidthInPixelsBoxed(int ordinal) {
        return typeAPI.getWidthInPixelsBoxed(ordinal);
    }

    public int getHeightInPixels(int ordinal) {
        return typeAPI.getHeightInPixels(ordinal);
    }

    public Integer getHeightInPixelsBoxed(int ordinal) {
        return typeAPI.getHeightInPixelsBoxed(ordinal);
    }

    public int getPixelAspectRatioWidth(int ordinal) {
        return typeAPI.getPixelAspectRatioWidth(ordinal);
    }

    public Integer getPixelAspectRatioWidthBoxed(int ordinal) {
        return typeAPI.getPixelAspectRatioWidthBoxed(ordinal);
    }

    public int getPixelAspectRatioHeight(int ordinal) {
        return typeAPI.getPixelAspectRatioHeight(ordinal);
    }

    public Integer getPixelAspectRatioHeightBoxed(int ordinal) {
        return typeAPI.getPixelAspectRatioHeightBoxed(ordinal);
    }

    public int getTargetWidthInPixels(int ordinal) {
        return typeAPI.getTargetWidthInPixels(ordinal);
    }

    public Integer getTargetWidthInPixelsBoxed(int ordinal) {
        return typeAPI.getTargetWidthInPixelsBoxed(ordinal);
    }

    public int getTargetHeightInPixels(int ordinal) {
        return typeAPI.getTargetHeightInPixels(ordinal);
    }

    public Integer getTargetHeightInPixelsBoxed(int ordinal) {
        return typeAPI.getTargetHeightInPixelsBoxed(ordinal);
    }

    public StreamDimensionsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}