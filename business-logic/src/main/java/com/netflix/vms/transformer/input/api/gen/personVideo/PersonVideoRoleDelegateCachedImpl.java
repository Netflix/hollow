package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PersonVideoRoleDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, PersonVideoRoleDelegate {

    private final Integer sequenceNumber;
    private final Integer roleTypeId;
    private final Long videoId;
    private PersonVideoRoleTypeAPI typeAPI;

    public PersonVideoRoleDelegateCachedImpl(PersonVideoRoleTypeAPI typeAPI, int ordinal) {
        this.sequenceNumber = typeAPI.getSequenceNumberBoxed(ordinal);
        this.roleTypeId = typeAPI.getRoleTypeIdBoxed(ordinal);
        this.videoId = typeAPI.getVideoIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getSequenceNumber(int ordinal) {
        if(sequenceNumber == null)
            return Integer.MIN_VALUE;
        return sequenceNumber.intValue();
    }

    public Integer getSequenceNumberBoxed(int ordinal) {
        return sequenceNumber;
    }

    public int getRoleTypeId(int ordinal) {
        if(roleTypeId == null)
            return Integer.MIN_VALUE;
        return roleTypeId.intValue();
    }

    public Integer getRoleTypeIdBoxed(int ordinal) {
        return roleTypeId;
    }

    public long getVideoId(int ordinal) {
        if(videoId == null)
            return Long.MIN_VALUE;
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

    public PersonVideoRoleTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (PersonVideoRoleTypeAPI) typeAPI;
    }

}