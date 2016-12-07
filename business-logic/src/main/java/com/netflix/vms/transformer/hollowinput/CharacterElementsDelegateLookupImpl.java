package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CharacterElementsDelegateLookupImpl extends HollowObjectAbstractDelegate implements CharacterElementsDelegate {

    private final CharacterElementsTypeAPI typeAPI;

    public CharacterElementsDelegateLookupImpl(CharacterElementsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getCharacterNameOrdinal(int ordinal) {
        return typeAPI.getCharacterNameOrdinal(ordinal);
    }

    public int getBladeBottomLineOrdinal(int ordinal) {
        return typeAPI.getBladeBottomLineOrdinal(ordinal);
    }

    public int getCharacterBioOrdinal(int ordinal) {
        return typeAPI.getCharacterBioOrdinal(ordinal);
    }

    public int getBladeTopLineOrdinal(int ordinal) {
        return typeAPI.getBladeTopLineOrdinal(ordinal);
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