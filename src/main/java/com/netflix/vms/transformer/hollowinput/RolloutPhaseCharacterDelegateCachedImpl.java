package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class RolloutPhaseCharacterDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutPhaseCharacterDelegate {

    private final Long sequenceNumber;
    private final Long roleId;
    private final Long personId;
    private final Long characterId;
   private RolloutPhaseCharacterTypeAPI typeAPI;

    public RolloutPhaseCharacterDelegateCachedImpl(RolloutPhaseCharacterTypeAPI typeAPI, int ordinal) {
        this.sequenceNumber = typeAPI.getSequenceNumberBoxed(ordinal);
        this.roleId = typeAPI.getRoleIdBoxed(ordinal);
        this.personId = typeAPI.getPersonIdBoxed(ordinal);
        this.characterId = typeAPI.getCharacterIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getSequenceNumber(int ordinal) {
        return sequenceNumber.longValue();
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return sequenceNumber;
    }

    public long getRoleId(int ordinal) {
        return roleId.longValue();
    }

    public Long getRoleIdBoxed(int ordinal) {
        return roleId;
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

    public RolloutPhaseCharacterTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RolloutPhaseCharacterTypeAPI) typeAPI;
    }

}