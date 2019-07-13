package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PersonVideoRoleDelegateLookupImpl extends HollowObjectAbstractDelegate implements PersonVideoRoleDelegate {

    private final PersonVideoRoleTypeAPI typeAPI;

    public PersonVideoRoleDelegateLookupImpl(PersonVideoRoleTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getSequenceNumber(int ordinal) {
        return typeAPI.getSequenceNumber(ordinal);
    }

    public Integer getSequenceNumberBoxed(int ordinal) {
        return typeAPI.getSequenceNumberBoxed(ordinal);
    }

    public int getRoleTypeId(int ordinal) {
        return typeAPI.getRoleTypeId(ordinal);
    }

    public Integer getRoleTypeIdBoxed(int ordinal) {
        return typeAPI.getRoleTypeIdBoxed(ordinal);
    }

    public long getVideoId(int ordinal) {
        return typeAPI.getVideoId(ordinal);
    }

    public Long getVideoIdBoxed(int ordinal) {
        return typeAPI.getVideoIdBoxed(ordinal);
    }

    public PersonVideoRoleTypeAPI getTypeAPI() {
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