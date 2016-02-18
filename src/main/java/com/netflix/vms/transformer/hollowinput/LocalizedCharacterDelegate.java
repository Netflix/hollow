package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface LocalizedCharacterDelegate extends HollowObjectDelegate {

    public long getCharacterId(int ordinal);

    public Long getCharacterIdBoxed(int ordinal);

    public int getTranslatedTextsOrdinal(int ordinal);

    public int getLabelOrdinal(int ordinal);

    public int getAttributeNameOrdinal(int ordinal);

    public int getLastUpdatedOrdinal(int ordinal);

    public LocalizedCharacterTypeAPI getTypeAPI();

}