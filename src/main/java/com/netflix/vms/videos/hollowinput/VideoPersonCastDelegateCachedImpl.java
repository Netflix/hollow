package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoPersonCastDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoPersonCastDelegate {

    private final Long sequenceNumber;
    private final Long roleTypeId;
    private final int roleNameOrdinal;
    private final Long videoId;
   private VideoPersonCastTypeAPI typeAPI;

    public VideoPersonCastDelegateCachedImpl(VideoPersonCastTypeAPI typeAPI, int ordinal) {
        this.sequenceNumber = typeAPI.getSequenceNumberBoxed(ordinal);
        this.roleTypeId = typeAPI.getRoleTypeIdBoxed(ordinal);
        this.roleNameOrdinal = typeAPI.getRoleNameOrdinal(ordinal);
        this.videoId = typeAPI.getVideoIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getSequenceNumber(int ordinal) {
        return sequenceNumber.longValue();
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return sequenceNumber;
    }

    public long getRoleTypeId(int ordinal) {
        return roleTypeId.longValue();
    }

    public Long getRoleTypeIdBoxed(int ordinal) {
        return roleTypeId;
    }

    public int getRoleNameOrdinal(int ordinal) {
        return roleNameOrdinal;
    }

    public long getVideoId(int ordinal) {
        return videoId.longValue();
    }

    public Long getVideoIdBoxed(int ordinal) {
        return videoId;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoPersonCastTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoPersonCastTypeAPI) typeAPI;
    }

}