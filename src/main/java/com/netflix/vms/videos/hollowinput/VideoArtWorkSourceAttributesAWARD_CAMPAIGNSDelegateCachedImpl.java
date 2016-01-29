package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoArtWorkSourceAttributesAWARD_CAMPAIGNSDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoArtWorkSourceAttributesAWARD_CAMPAIGNSDelegate {

    private final int valueOrdinal;
   private VideoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI typeAPI;

    public VideoArtWorkSourceAttributesAWARD_CAMPAIGNSDelegateCachedImpl(VideoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI typeAPI, int ordinal) {
        this.valueOrdinal = typeAPI.getValueOrdinal(ordinal);
        this.typeAPI = typeAPI;
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

    public VideoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI) typeAPI;
    }

}