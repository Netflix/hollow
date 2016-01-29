package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class CharacterElementsDelegateLookupImpl extends HollowObjectAbstractDelegate implements CharacterElementsDelegate {

    private final CharacterElementsTypeAPI typeAPI;

    public CharacterElementsDelegateLookupImpl(CharacterElementsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getCharacter_NameOrdinal(int ordinal) {
        return typeAPI.getCharacter_NameOrdinal(ordinal);
    }

    public int getBlade_Bottom_LineOrdinal(int ordinal) {
        return typeAPI.getBlade_Bottom_LineOrdinal(ordinal);
    }

    public int getCharacter_BioOrdinal(int ordinal) {
        return typeAPI.getCharacter_BioOrdinal(ordinal);
    }

    public int getBlade_Top_LineOrdinal(int ordinal) {
        return typeAPI.getBlade_Top_LineOrdinal(ordinal);
    }

    public CharacterElementsTypeAPI getTypeAPI() {
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