package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoStreamCropParamsDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoStreamCropParamsDelegate {

    private final VideoStreamCropParamsTypeAPI typeAPI;

    public VideoStreamCropParamsDelegateLookupImpl(VideoStreamCropParamsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getX(int ordinal) {
        return typeAPI.getX(ordinal);
    }

    public Integer getXBoxed(int ordinal) {
        return typeAPI.getXBoxed(ordinal);
    }

    public int getY(int ordinal) {
        return typeAPI.getY(ordinal);
    }

    public Integer getYBoxed(int ordinal) {
        return typeAPI.getYBoxed(ordinal);
    }

    public int getWidth(int ordinal) {
        return typeAPI.getWidth(ordinal);
    }

    public Integer getWidthBoxed(int ordinal) {
        return typeAPI.getWidthBoxed(ordinal);
    }

    public int getHeight(int ordinal) {
        return typeAPI.getHeight(ordinal);
    }

    public Integer getHeightBoxed(int ordinal) {
        return typeAPI.getHeightBoxed(ordinal);
    }

    public VideoStreamCropParamsTypeAPI getTypeAPI() {
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