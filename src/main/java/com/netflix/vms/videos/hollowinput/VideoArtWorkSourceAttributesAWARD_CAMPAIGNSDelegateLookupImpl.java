package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoArtWorkSourceAttributesAWARD_CAMPAIGNSDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoArtWorkSourceAttributesAWARD_CAMPAIGNSDelegate {

    private final VideoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI typeAPI;

    public VideoArtWorkSourceAttributesAWARD_CAMPAIGNSDelegateLookupImpl(VideoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getValueOrdinal(int ordinal) {
        return typeAPI.getValueOrdinal(ordinal);
    }

    public VideoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI getTypeAPI() {
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