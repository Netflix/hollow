package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PersonCharacterResourceDelegateLookupImpl extends HollowObjectAbstractDelegate implements PersonCharacterResourceDelegate {

    private final PersonCharacterResourceTypeAPI typeAPI;

    public PersonCharacterResourceDelegateLookupImpl(PersonCharacterResourceTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getId(int ordinal) {
        return typeAPI.getId(ordinal);
    }

    public Long getIdBoxed(int ordinal) {
        return typeAPI.getIdBoxed(ordinal);
    }

    public int getPrefixOrdinal(int ordinal) {
        return typeAPI.getPrefixOrdinal(ordinal);
    }

    public int getCnOrdinal(int ordinal) {
        return typeAPI.getCnOrdinal(ordinal);
    }

    public PersonCharacterResourceTypeAPI getTypeAPI() {
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