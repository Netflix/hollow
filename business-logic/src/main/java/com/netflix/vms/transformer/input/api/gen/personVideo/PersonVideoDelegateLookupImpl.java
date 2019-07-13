package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PersonVideoDelegateLookupImpl extends HollowObjectAbstractDelegate implements PersonVideoDelegate {

    private final PersonVideoTypeAPI typeAPI;

    public PersonVideoDelegateLookupImpl(PersonVideoTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getAliasIdsOrdinal(int ordinal) {
        return typeAPI.getAliasIdsOrdinal(ordinal);
    }

    public int getRolesOrdinal(int ordinal) {
        return typeAPI.getRolesOrdinal(ordinal);
    }

    public long getPersonId(int ordinal) {
        return typeAPI.getPersonId(ordinal);
    }

    public Long getPersonIdBoxed(int ordinal) {
        return typeAPI.getPersonIdBoxed(ordinal);
    }

    public PersonVideoTypeAPI getTypeAPI() {
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