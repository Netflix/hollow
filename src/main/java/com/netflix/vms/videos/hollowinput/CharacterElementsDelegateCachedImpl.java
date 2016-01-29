package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class CharacterElementsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, CharacterElementsDelegate {

    private final int Character_NameOrdinal;
    private final int Blade_Bottom_LineOrdinal;
    private final int Character_BioOrdinal;
    private final int Blade_Top_LineOrdinal;
   private CharacterElementsTypeAPI typeAPI;

    public CharacterElementsDelegateCachedImpl(CharacterElementsTypeAPI typeAPI, int ordinal) {
        this.Character_NameOrdinal = typeAPI.getCharacter_NameOrdinal(ordinal);
        this.Blade_Bottom_LineOrdinal = typeAPI.getBlade_Bottom_LineOrdinal(ordinal);
        this.Character_BioOrdinal = typeAPI.getCharacter_BioOrdinal(ordinal);
        this.Blade_Top_LineOrdinal = typeAPI.getBlade_Top_LineOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getCharacter_NameOrdinal(int ordinal) {
        return Character_NameOrdinal;
    }

    public int getBlade_Bottom_LineOrdinal(int ordinal) {
        return Blade_Bottom_LineOrdinal;
    }

    public int getCharacter_BioOrdinal(int ordinal) {
        return Character_BioOrdinal;
    }

    public int getBlade_Top_LineOrdinal(int ordinal) {
        return Blade_Top_LineOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public CharacterElementsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (CharacterElementsTypeAPI) typeAPI;
    }

}