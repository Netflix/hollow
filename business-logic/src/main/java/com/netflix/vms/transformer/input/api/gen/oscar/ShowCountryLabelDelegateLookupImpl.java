package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ShowCountryLabelDelegateLookupImpl extends HollowObjectAbstractDelegate implements ShowCountryLabelDelegate {

    private final ShowCountryLabelTypeAPI typeAPI;

    public ShowCountryLabelDelegateLookupImpl(ShowCountryLabelTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getVideoId(int ordinal) {
        return typeAPI.getVideoId(ordinal);
    }

    public Long getVideoIdBoxed(int ordinal) {
        return typeAPI.getVideoIdBoxed(ordinal);
    }

    public int getShowMemberTypesOrdinal(int ordinal) {
        return typeAPI.getShowMemberTypesOrdinal(ordinal);
    }

    public ShowCountryLabelTypeAPI getTypeAPI() {
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