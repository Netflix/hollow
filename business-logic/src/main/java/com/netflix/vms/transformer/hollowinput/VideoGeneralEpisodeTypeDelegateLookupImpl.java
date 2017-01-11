package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoGeneralEpisodeTypeDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoGeneralEpisodeTypeDelegate {

    private final VideoGeneralEpisodeTypeTypeAPI typeAPI;

    public VideoGeneralEpisodeTypeDelegateLookupImpl(VideoGeneralEpisodeTypeTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getValueOrdinal(int ordinal) {
        return typeAPI.getValueOrdinal(ordinal);
    }

    public VideoGeneralEpisodeTypeTypeAPI getTypeAPI() {
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