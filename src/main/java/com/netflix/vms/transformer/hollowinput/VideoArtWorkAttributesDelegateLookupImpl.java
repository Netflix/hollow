package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoArtWorkAttributesDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoArtWorkAttributesDelegate {

    private final VideoArtWorkAttributesTypeAPI typeAPI;

    public VideoArtWorkAttributesDelegateLookupImpl(VideoArtWorkAttributesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getNameOrdinal(int ordinal) {
        return typeAPI.getNameOrdinal(ordinal);
    }

    public int getValueOrdinal(int ordinal) {
        return typeAPI.getValueOrdinal(ordinal);
    }

    public VideoArtWorkAttributesTypeAPI getTypeAPI() {
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