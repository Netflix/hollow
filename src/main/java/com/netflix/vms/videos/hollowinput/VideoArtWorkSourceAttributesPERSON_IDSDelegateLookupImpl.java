package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoArtWorkSourceAttributesPERSON_IDSDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoArtWorkSourceAttributesPERSON_IDSDelegate {

    private final VideoArtWorkSourceAttributesPERSON_IDSTypeAPI typeAPI;

    public VideoArtWorkSourceAttributesPERSON_IDSDelegateLookupImpl(VideoArtWorkSourceAttributesPERSON_IDSTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getValueOrdinal(int ordinal) {
        return typeAPI.getValueOrdinal(ordinal);
    }

    public VideoArtWorkSourceAttributesPERSON_IDSTypeAPI getTypeAPI() {
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