package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class CharacterElementsTypeAPI extends HollowObjectTypeAPI {

    private final CharacterElementsDelegateLookupImpl delegateLookupImpl;

    CharacterElementsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "Character Name",
            "Blade Bottom Line",
            "Character Bio",
            "Blade Top Line"
        });
        this.delegateLookupImpl = new CharacterElementsDelegateLookupImpl(this);
    }

    public int getCharacter_NameOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("CharacterElements", ordinal, "Character_Name");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getCharacter_NameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getBlade_Bottom_LineOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("CharacterElements", ordinal, "Blade_Bottom_Line");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getBlade_Bottom_LineTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getCharacter_BioOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("CharacterElements", ordinal, "Character_Bio");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getCharacter_BioTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getBlade_Top_LineOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("CharacterElements", ordinal, "Blade_Top_Line");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StringTypeAPI getBlade_Top_LineTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public CharacterElementsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}