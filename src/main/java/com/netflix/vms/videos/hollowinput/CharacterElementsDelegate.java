package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface CharacterElementsDelegate extends HollowObjectDelegate {

    public int getCharacter_NameOrdinal(int ordinal);

    public int getBlade_Bottom_LineOrdinal(int ordinal);

    public int getCharacter_BioOrdinal(int ordinal);

    public int getBlade_Top_LineOrdinal(int ordinal);

    public CharacterElementsTypeAPI getTypeAPI();

}