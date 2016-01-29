package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoTypeDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoTypeDelegate {

    private final VideoTypeTypeAPI typeAPI;

    public VideoTypeDelegateLookupImpl(VideoTypeTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getVideoId(int ordinal) {
        return typeAPI.getVideoId(ordinal);
    }

    public Long getVideoIdBoxed(int ordinal) {
        return typeAPI.getVideoIdBoxed(ordinal);
    }

    public int getTypeOrdinal(int ordinal) {
        return typeAPI.getTypeOrdinal(ordinal);
    }

    public boolean getIsTV(int ordinal) {
        return typeAPI.getIsTV(ordinal);
    }

    public Boolean getIsTVBoxed(int ordinal) {
        return typeAPI.getIsTVBoxed(ordinal);
    }

    public VideoTypeTypeAPI getTypeAPI() {
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