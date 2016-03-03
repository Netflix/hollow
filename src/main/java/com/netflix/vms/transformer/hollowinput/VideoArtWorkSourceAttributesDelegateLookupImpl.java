package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoArtWorkSourceAttributesDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoArtWorkSourceAttributesDelegate {

    private final VideoArtWorkSourceAttributesTypeAPI typeAPI;

    public VideoArtWorkSourceAttributesDelegateLookupImpl(VideoArtWorkSourceAttributesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getPassthroughOrdinal(int ordinal) {
        return typeAPI.getPassthroughOrdinal(ordinal);
    }

    public VideoArtWorkSourceAttributesTypeAPI getTypeAPI() {
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