package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RolloutPhaseCharacterTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhaseCharacterDelegateLookupImpl delegateLookupImpl;

    RolloutPhaseCharacterTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "sequenceNumber",
            "roleId",
            "personId",
            "characterId"
        });
        this.delegateLookupImpl = new RolloutPhaseCharacterDelegateLookupImpl(this);
    }

    public long getSequenceNumber(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("RolloutPhaseCharacter", ordinal, "sequenceNumber");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("RolloutPhaseCharacter", ordinal, "sequenceNumber");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getRoleId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("RolloutPhaseCharacter", ordinal, "roleId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getRoleIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("RolloutPhaseCharacter", ordinal, "roleId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getPersonId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("RolloutPhaseCharacter", ordinal, "personId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getPersonIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("RolloutPhaseCharacter", ordinal, "personId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getCharacterId(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("RolloutPhaseCharacter", ordinal, "characterId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getCharacterIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("RolloutPhaseCharacter", ordinal, "characterId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public RolloutPhaseCharacterDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}