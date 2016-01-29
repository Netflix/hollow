package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class CharactersTypeAPI extends HollowObjectTypeAPI {

    private final CharactersDelegateLookupImpl delegateLookupImpl;

    CharactersTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "b",
            "prefix",
            "id",
            "cn"
        });
        this.delegateLookupImpl = new CharactersDelegateLookupImpl(this);
    }

    public int getBOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("Characters", ordinal, "b");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public CharactersBTypeAPI getBTypeAPI() {
        return getAPI().getCharactersBTypeAPI();
    }

    public int getPrefixOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Characters", ordinal, "prefix");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getPrefixTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("Characters", ordinal, "id");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("Characters", ordinal, "id");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getCnOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("Characters", ordinal, "cn");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public CharactersCnTypeAPI getCnTypeAPI() {
        return getAPI().getCharactersCnTypeAPI();
    }

    public CharactersDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}