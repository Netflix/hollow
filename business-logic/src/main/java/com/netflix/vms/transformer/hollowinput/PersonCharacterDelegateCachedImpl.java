package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class PersonCharacterDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, PersonCharacterDelegate {

    private final Long personId;
    private final Long characterId;
   private PersonCharacterTypeAPI typeAPI;

    public PersonCharacterDelegateCachedImpl(PersonCharacterTypeAPI typeAPI, int ordinal) {
        this.personId = typeAPI.getPersonIdBoxed(ordinal);
        this.characterId = typeAPI.getCharacterIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getPersonId(int ordinal) {
        return personId.longValue();
    }

    public Long getPersonIdBoxed(int ordinal) {
        return personId;
    }

    public long getCharacterId(int ordinal) {
        return characterId.longValue();
    }

    public Long getCharacterIdBoxed(int ordinal) {
        return characterId;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public PersonCharacterTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (PersonCharacterTypeAPI) typeAPI;
    }

}