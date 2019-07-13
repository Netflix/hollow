package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PersonVideoAliasIdDelegateLookupImpl extends HollowObjectAbstractDelegate implements PersonVideoAliasIdDelegate {

    private final PersonVideoAliasIdTypeAPI typeAPI;

    public PersonVideoAliasIdDelegateLookupImpl(PersonVideoAliasIdTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getValue(int ordinal) {
        return typeAPI.getValue(ordinal);
    }

    public Integer getValueBoxed(int ordinal) {
        return typeAPI.getValueBoxed(ordinal);
    }

    public PersonVideoAliasIdTypeAPI getTypeAPI() {
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