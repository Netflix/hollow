package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class PersonVideoAliasIdDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, PersonVideoAliasIdDelegate {

    private final Integer value;
   private PersonVideoAliasIdTypeAPI typeAPI;

    public PersonVideoAliasIdDelegateCachedImpl(PersonVideoAliasIdTypeAPI typeAPI, int ordinal) {
        this.value = typeAPI.getValueBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getValue(int ordinal) {
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