package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ImageStreamInfoDelegateLookupImpl extends HollowObjectAbstractDelegate implements ImageStreamInfoDelegate {

    private final ImageStreamInfoTypeAPI typeAPI;

    public ImageStreamInfoDelegateLookupImpl(ImageStreamInfoTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getImageCount(int ordinal) {
        return typeAPI.getImageCount(ordinal);
    }

    public Integer getImageCountBoxed(int ordinal) {
        return typeAPI.getImageCountBoxed(ordinal);
    }

    public int getImageFormatOrdinal(int ordinal) {
        return typeAPI.getImageFormatOrdinal(ordinal);
    }

    public long getOffsetMillis(int ordinal) {
        return typeAPI.getOffsetMillis(ordinal);
    }

    public Long getOffsetMillisBoxed(int ordinal) {
        return typeAPI.getOffsetMillisBoxed(ordinal);
    }

    public ImageStreamInfoTypeAPI getTypeAPI() {
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