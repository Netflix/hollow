package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoArtWorkAttributesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoArtWorkAttributesDelegate {

    private final int nameOrdinal;
    private final int valueOrdinal;
   private VideoArtWorkAttributesTypeAPI typeAPI;

    public VideoArtWorkAttributesDelegateCachedImpl(VideoArtWorkAttributesTypeAPI typeAPI, int ordinal) {
        this.nameOrdinal = typeAPI.getNameOrdinal(ordinal);
        this.valueOrdinal = typeAPI.getValueOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getNameOrdinal(int ordinal) {
        return nameOrdinal;
    }

    public int getValueOrdinal(int ordinal) {
        return valueOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoArtWorkAttributesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoArtWorkAttributesTypeAPI) typeAPI;
    }

}