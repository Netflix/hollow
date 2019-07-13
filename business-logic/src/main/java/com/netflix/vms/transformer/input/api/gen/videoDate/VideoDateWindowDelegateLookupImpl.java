package com.netflix.vms.transformer.input.api.gen.videoDate;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoDateWindowDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoDateWindowDelegate {

    private final VideoDateWindowTypeAPI typeAPI;

    public VideoDateWindowDelegateLookupImpl(VideoDateWindowTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getCountryCode(int ordinal) {
        ordinal = typeAPI.getCountryCodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isCountryCodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getCountryCodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return typeAPI.getCountryCodeOrdinal(ordinal);
    }

    public int getReleaseDatesOrdinal(int ordinal) {
        return typeAPI.getReleaseDatesOrdinal(ordinal);
    }

    public VideoDateWindowTypeAPI getTypeAPI() {
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