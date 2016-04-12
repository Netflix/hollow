package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class PersonAliasesDelegateLookupImpl extends HollowObjectAbstractDelegate implements PersonAliasesDelegate {

    private final PersonAliasesTypeAPI typeAPI;

    public PersonAliasesDelegateLookupImpl(PersonAliasesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getAliasId(int ordinal) {
        return typeAPI.getAliasId(ordinal);
    }

    public Long getAliasIdBoxed(int ordinal) {
        return typeAPI.getAliasIdBoxed(ordinal);
    }

    public int getNameOrdinal(int ordinal) {
        return typeAPI.getNameOrdinal(ordinal);
    }

    public PersonAliasesTypeAPI getTypeAPI() {
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