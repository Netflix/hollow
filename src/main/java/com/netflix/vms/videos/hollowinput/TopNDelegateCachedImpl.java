package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class TopNDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, TopNDelegate {

    private final Long videoId;
    private final int attributesOrdinal;
    private final int dseSourceFileOrdinal;
   private TopNTypeAPI typeAPI;

    public TopNDelegateCachedImpl(TopNTypeAPI typeAPI, int ordinal) {
        this.videoId = typeAPI.getVideoIdBoxed(ordinal);
        this.attributesOrdinal = typeAPI.getAttributesOrdinal(ordinal);
        this.dseSourceFileOrdinal = typeAPI.getDseSourceFileOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getVideoId(int ordinal) {
        return videoId.longValue();
    }

    public Long getVideoIdBoxed(int ordinal) {
        return videoId;
    }

    public int getAttributesOrdinal(int ordinal) {
        return attributesOrdinal;
    }

    public int getDseSourceFileOrdinal(int ordinal) {
        return dseSourceFileOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public TopNTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (TopNTypeAPI) typeAPI;
    }

}