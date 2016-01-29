package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface LocalizedCharacterDelegate extends HollowObjectDelegate {

    public long getLastUpdated(int ordinal);

    public Long getLastUpdatedBoxed(int ordinal);

    public int getTranslatedTextsOrdinal(int ordinal);

    public int getAttributeNameOrdinal(int ordinal);

    public int getLabelOrdinal(int ordinal);

    public long getCharacterId(int ordinal);

    public Long getCharacterIdBoxed(int ordinal);

    public LocalizedCharacterTypeAPI getTypeAPI();

}