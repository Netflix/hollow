package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class ShowCountryLabelDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ShowCountryLabelDelegate {

    private final Long videoId;
    private final int showMemberTypesOrdinal;
    private ShowCountryLabelTypeAPI typeAPI;

    public ShowCountryLabelDelegateCachedImpl(ShowCountryLabelTypeAPI typeAPI, int ordinal) {
        this.videoId = typeAPI.getVideoIdBoxed(ordinal);
        this.showMemberTypesOrdinal = typeAPI.getShowMemberTypesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getVideoId(int ordinal) {
        if(videoId == null)
            return Long.MIN_VALUE;
        return videoId.longValue();
    }

    public Long getVideoIdBoxed(int ordinal) {
        return videoId;
    }

    public int getShowMemberTypesOrdinal(int ordinal) {
        return showMemberTypesOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public ShowCountryLabelTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ShowCountryLabelTypeAPI) typeAPI;
    }

}