package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class PersonVideoRoleTypeAPI extends HollowObjectTypeAPI {

    private final PersonVideoRoleDelegateLookupImpl delegateLookupImpl;

    public PersonVideoRoleTypeAPI(PersonVideoAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "sequenceNumber",
            "roleTypeId",
            "videoId"
        });
        this.delegateLookupImpl = new PersonVideoRoleDelegateLookupImpl(this);
    }

    public int getSequenceNumber(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleInt("PersonVideoRole", ordinal, "sequenceNumber");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
    }

    public Integer getSequenceNumberBoxed(int ordinal) {
        int i;
        if(fieldIndex[0] == -1) {
            i = missingDataHandler().handleInt("PersonVideoRole", ordinal, "sequenceNumber");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getRoleTypeId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleInt("PersonVideoRole", ordinal, "roleTypeId");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
    }

    public Integer getRoleTypeIdBoxed(int ordinal) {
        int i;
        if(fieldIndex[1] == -1) {
            i = missingDataHandler().handleInt("PersonVideoRole", ordinal, "roleTypeId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public long getVideoId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("PersonVideoRole", ordinal, "videoId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getVideoIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("PersonVideoRole", ordinal, "videoId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public PersonVideoRoleDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public PersonVideoAPI getAPI() {
        return (PersonVideoAPI) api;
    }

}