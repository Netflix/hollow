package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

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