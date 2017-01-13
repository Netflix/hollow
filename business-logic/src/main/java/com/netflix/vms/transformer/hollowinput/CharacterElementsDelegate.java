package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface CharacterElementsDelegate extends HollowObjectDelegate {

    public int getCharacterNameOrdinal(int ordinal);

    public int getBladeBottomLineOrdinal(int ordinal);

    public int getCharacterBioOrdinal(int ordinal);

    public int getBladeTopLineOrdinal(int ordinal);

    public CharacterElementsTypeAPI getTypeAPI();

}