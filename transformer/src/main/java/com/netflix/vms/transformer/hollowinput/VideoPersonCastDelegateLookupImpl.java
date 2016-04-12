package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoPersonCastDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoPersonCastDelegate {

    private final VideoPersonCastTypeAPI typeAPI;

    public VideoPersonCastDelegateLookupImpl(VideoPersonCastTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getVideoId(int ordinal) {
        return typeAPI.getVideoId(ordinal);
    }

    public Long getVideoIdBoxed(int ordinal) {
        return typeAPI.getVideoIdBoxed(ordinal);
    }

    public long getRoleTypeId(int ordinal) {
        return typeAPI.getRoleTypeId(ordinal);
    }

    public Long getRoleTypeIdBoxed(int ordinal) {
        return typeAPI.getRoleTypeIdBoxed(ordinal);
    }

    public long getSequenceNumber(int ordinal) {
        return typeAPI.getSequenceNumber(ordinal);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return typeAPI.getSequenceNumberBoxed(ordinal);
    }

    public int getRoleNameOrdinal(int ordinal) {
        return typeAPI.getRoleNameOrdinal(ordinal);
    }

    public VideoPersonCastTypeAPI getTypeAPI() {
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