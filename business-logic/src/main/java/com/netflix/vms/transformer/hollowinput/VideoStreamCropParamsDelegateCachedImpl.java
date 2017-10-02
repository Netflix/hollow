package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class VideoStreamCropParamsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoStreamCropParamsDelegate {

    private final Integer x;
    private final Integer y;
    private final Integer width;
    private final Integer height;
    private VideoStreamCropParamsTypeAPI typeAPI;

    public VideoStreamCropParamsDelegateCachedImpl(VideoStreamCropParamsTypeAPI typeAPI, int ordinal) {
        this.x = typeAPI.getXBoxed(ordinal);
        this.y = typeAPI.getYBoxed(ordinal);
        this.width = typeAPI.getWidthBoxed(ordinal);
        this.height = typeAPI.getHeightBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getX(int ordinal) {
        if(x == null)
            return Integer.MIN_VALUE;
        return x.intValue();
    }

    public Integer getXBoxed(int ordinal) {
        return x;
    }

    public int getY(int ordinal) {
        if(y == null)
            return Integer.MIN_VALUE;
        return y.intValue();
    }

    public Integer getYBoxed(int ordinal) {
        return y;
    }

    public int getWidth(int ordinal) {
        if(width == null)
            return Integer.MIN_VALUE;
        return width.intValue();
    }

    public Integer getWidthBoxed(int ordinal) {
        return width;
    }

    public int getHeight(int ordinal) {
        if(height == null)
            return Integer.MIN_VALUE;
        return height.intValue();
    }

    public Integer getHeightBoxed(int ordinal) {
        return height;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoStreamCropParamsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoStreamCropParamsTypeAPI) typeAPI;
    }

}