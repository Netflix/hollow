package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class PersonVideoDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, PersonVideoDelegate {

    private final int aliasIdsOrdinal;
    private final int rolesOrdinal;
    private final Long personId;
   private PersonVideoTypeAPI typeAPI;

    public PersonVideoDelegateCachedImpl(PersonVideoTypeAPI typeAPI, int ordinal) {
        this.aliasIdsOrdinal = typeAPI.getAliasIdsOrdinal(ordinal);
        this.rolesOrdinal = typeAPI.getRolesOrdinal(ordinal);
        this.personId = typeAPI.getPersonIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getAliasIdsOrdinal(int ordinal) {
        return aliasIdsOrdinal;
    }

    public int getRolesOrdinal(int ordinal) {
        return rolesOrdinal;
    }

    public long getPersonId(int ordinal) {
        return personId.longValue();
    }

    public Long getPersonIdBoxed(int ordinal) {
        return personId;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public PersonVideoTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (PersonVideoTypeAPI) typeAPI;
    }

}