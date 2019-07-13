package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PersonVideoAliasIdDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, PersonVideoAliasIdDelegate {

    private final Integer value;
    private PersonVideoAliasIdTypeAPI typeAPI;

    public PersonVideoAliasIdDelegateCachedImpl(PersonVideoAliasIdTypeAPI typeAPI, int ordinal) {
        this.value = typeAPI.getValueBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getValue(int ordinal) {
        if(value == null)
            return Integer.MIN_VALUE;
        return value.intValue();
    }

    public Integer getValueBoxed(int ordinal) {
        return value;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public PersonVideoAliasIdTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (PersonVideoAliasIdTypeAPI) typeAPI;
    }

}