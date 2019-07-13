package com.netflix.vms.transformer.input.api.gen.videoType;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoTypeMediaDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoTypeMediaDelegate {

    private final VideoTypeMediaTypeAPI typeAPI;

    public VideoTypeMediaDelegateLookupImpl(VideoTypeMediaTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getValue(int ordinal) {
        ordinal = typeAPI.getValueOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isValueEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getValueOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getValueOrdinal(int ordinal) {
        return typeAPI.getValueOrdinal(ordinal);
    }

    public VideoTypeMediaTypeAPI getTypeAPI() {
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