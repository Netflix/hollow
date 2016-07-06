package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class PersonsDelegateLookupImpl extends HollowObjectAbstractDelegate implements PersonsDelegate {

    private final PersonsTypeAPI typeAPI;

    public PersonsDelegateLookupImpl(PersonsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getPersonId(int ordinal) {
        return typeAPI.getPersonId(ordinal);
    }

    public Long getPersonIdBoxed(int ordinal) {
        return typeAPI.getPersonIdBoxed(ordinal);
    }

    public int getNameOrdinal(int ordinal) {
        return typeAPI.getNameOrdinal(ordinal);
    }

    public int getBioOrdinal(int ordinal) {
        return typeAPI.getBioOrdinal(ordinal);
    }

    public PersonsTypeAPI getTypeAPI() {
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