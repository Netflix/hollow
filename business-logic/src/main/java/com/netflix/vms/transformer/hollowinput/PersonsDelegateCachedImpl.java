package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class PersonsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, PersonsDelegate {

    private final Long personId;
    private final int nameOrdinal;
    private final int bioOrdinal;
    private PersonsTypeAPI typeAPI;

    public PersonsDelegateCachedImpl(PersonsTypeAPI typeAPI, int ordinal) {
        this.personId = typeAPI.getPersonIdBoxed(ordinal);
        this.nameOrdinal = typeAPI.getNameOrdinal(ordinal);
        this.bioOrdinal = typeAPI.getBioOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getPersonId(int ordinal) {
        if(personId == null)
            return Long.MIN_VALUE;
        return personId.longValue();
    }

    public Long getPersonIdBoxed(int ordinal) {
        return personId;
    }

    public int getNameOrdinal(int ordinal) {
        return nameOrdinal;
    }

    public int getBioOrdinal(int ordinal) {
        return bioOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public PersonsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (PersonsTypeAPI) typeAPI;
    }

}