package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhaseCharacterDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutPhaseCharacterDelegate {

    private final RolloutPhaseCharacterTypeAPI typeAPI;

    public RolloutPhaseCharacterDelegateLookupImpl(RolloutPhaseCharacterTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getSequenceNumber(int ordinal) {
        return typeAPI.getSequenceNumber(ordinal);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return typeAPI.getSequenceNumberBoxed(ordinal);
    }

    public long getRoleId(int ordinal) {
        return typeAPI.getRoleId(ordinal);
    }

    public Long getRoleIdBoxed(int ordinal) {
        return typeAPI.getRoleIdBoxed(ordinal);
    }

    public long getPersonId(int ordinal) {
        return typeAPI.getPersonId(ordinal);
    }

    public Long getPersonIdBoxed(int ordinal) {
        return typeAPI.getPersonIdBoxed(ordinal);
    }

    public long getCharacterId(int ordinal) {
        return typeAPI.getCharacterId(ordinal);
    }

    public Long getCharacterIdBoxed(int ordinal) {
        return typeAPI.getCharacterIdBoxed(ordinal);
    }

    public RolloutPhaseCharacterTypeAPI getTypeAPI() {
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