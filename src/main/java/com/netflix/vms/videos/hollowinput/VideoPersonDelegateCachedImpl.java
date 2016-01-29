package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoPersonDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoPersonDelegate {

    private final int castOrdinal;
    private final int aliasOrdinal;
    private final Long personId;
   private VideoPersonTypeAPI typeAPI;

    public VideoPersonDelegateCachedImpl(VideoPersonTypeAPI typeAPI, int ordinal) {
        this.castOrdinal = typeAPI.getCastOrdinal(ordinal);
        this.aliasOrdinal = typeAPI.getAliasOrdinal(ordinal);
        this.personId = typeAPI.getPersonIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getCastOrdinal(int ordinal) {
        return castOrdinal;
    }

    public int getAliasOrdinal(int ordinal) {
        return aliasOrdinal;
    }

    public long getPersonId(int ordinal) {
        return personId.longValue();
    }

    public Long getPersonIdBoxed(int ordinal) {
        return personId;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoPersonTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoPersonTypeAPI) typeAPI;
    }

}