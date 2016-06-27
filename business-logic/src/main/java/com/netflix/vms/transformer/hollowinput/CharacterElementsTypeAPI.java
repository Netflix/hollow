package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class CharacterElementsTypeAPI extends HollowObjectTypeAPI {

    private final CharacterElementsDelegateLookupImpl delegateLookupImpl;

    CharacterElementsTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "characterName",
            "bladeBottomLine",
            "characterBio",
            "bladeTopLine"
        });
        this.delegateLookupImpl = new CharacterElementsDelegateLookupImpl(this);
    }

    public int getCharacterNameOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("CharacterElements", ordinal, "characterName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getCharacterNameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getBladeBottomLineOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("CharacterElements", ordinal, "bladeBottomLine");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getBladeBottomLineTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getCharacterBioOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("CharacterElements", ordinal, "characterBio");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getCharacterBioTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getBladeTopLineOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("CharacterElements", ordinal, "bladeTopLine");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StringTypeAPI getBladeTopLineTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public CharacterElementsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}