package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoDateWindowDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoDateWindowDelegate {

    private final VideoDateWindowTypeAPI typeAPI;

    public VideoDateWindowDelegateLookupImpl(VideoDateWindowTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
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