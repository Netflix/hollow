package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ImageStreamInfoDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ImageStreamInfoDelegate {

    private final Integer imageCount;
    private final int imageFormatOrdinal;
    private final Long offsetMillis;
    private ImageStreamInfoTypeAPI typeAPI;

    public ImageStreamInfoDelegateCachedImpl(ImageStreamInfoTypeAPI typeAPI, int ordinal) {
        this.imageCount = typeAPI.getImageCountBoxed(ordinal);
        this.imageFormatOrdinal = typeAPI.getImageFormatOrdinal(ordinal);
        this.offsetMillis = typeAPI.getOffsetMillisBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getImageCount(int ordinal) {
        if(imageCount == null)
            return Integer.MIN_VALUE;
        return imageCount.intValue();
    }

    public Integer getImageCountBoxed(int ordinal) {
        return imageCount;
    }

    public int getImageFormatOrdinal(int ordinal) {
        return imageFormatOrdinal;
    }

    public long getOffsetMillis(int ordinal) {
        if(offsetMillis == null)
            return Long.MIN_VALUE;
        return offsetMillis.longValue();
    }

    public Long getOffsetMillisBoxed(int ordinal) {
        return offsetMillis;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public ImageStreamInfoTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ImageStreamInfoTypeAPI) typeAPI;
    }

}