package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoRatingAdvisoriesDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoRatingAdvisoriesDelegate {

    private final VideoRatingAdvisoriesTypeAPI typeAPI;

    public VideoRatingAdvisoriesDelegateLookupImpl(VideoRatingAdvisoriesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public boolean getOrdered(int ordinal) {
        return typeAPI.getOrdered(ordinal);
    }

    public Boolean getOrderedBoxed(int ordinal) {
        return typeAPI.getOrderedBoxed(ordinal);
    }

    public boolean getImageOnly(int ordinal) {
        return typeAPI.getImageOnly(ordinal);
    }

    public Boolean getImageOnlyBoxed(int ordinal) {
        return typeAPI.getImageOnlyBoxed(ordinal);
    }

    public int getIdsOrdinal(int ordinal) {
        return typeAPI.getIdsOrdinal(ordinal);
    }

    public VideoRatingAdvisoriesTypeAPI getTypeAPI() {
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