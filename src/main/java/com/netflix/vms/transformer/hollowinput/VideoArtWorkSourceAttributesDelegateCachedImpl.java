package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoArtWorkSourceAttributesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoArtWorkSourceAttributesDelegate {

    private final int passthroughOrdinal;
   private VideoArtWorkSourceAttributesTypeAPI typeAPI;

    public VideoArtWorkSourceAttributesDelegateCachedImpl(VideoArtWorkSourceAttributesTypeAPI typeAPI, int ordinal) {
        this.passthroughOrdinal = typeAPI.getPassthroughOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getPassthroughOrdinal(int ordinal) {
        return passthroughOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoArtWorkSourceAttributesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoArtWorkSourceAttributesTypeAPI) typeAPI;
    }

}