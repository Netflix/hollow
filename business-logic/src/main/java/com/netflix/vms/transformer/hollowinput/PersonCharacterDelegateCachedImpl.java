package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

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
        if(personId == null)
            return Long.MIN_VALUE;
        return personId.longValue();
    }

    public Long getPersonIdBoxed(int ordinal) {
        return personId;
    }

    public long getCharacterId(int ordinal) {
        if(characterId == null)
            return Long.MIN_VALUE;
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