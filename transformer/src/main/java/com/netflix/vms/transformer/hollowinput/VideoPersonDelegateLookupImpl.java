package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoPersonDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoPersonDelegate {

    private final VideoPersonTypeAPI typeAPI;

    public VideoPersonDelegateLookupImpl(VideoPersonTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getPersonId(int ordinal) {
        return typeAPI.getPersonId(ordinal);
    }

    public Long getPersonIdBoxed(int ordinal) {
        return typeAPI.getPersonIdBoxed(ordinal);
    }

    public int getCastOrdinal(int ordinal) {
        return typeAPI.getCastOrdinal(ordinal);
    }

    public int getAliasOrdinal(int ordinal) {
        return typeAPI.getAliasOrdinal(ordinal);
    }

    public VideoPersonTypeAPI getTypeAPI() {
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