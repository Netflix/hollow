package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface CharacterDelegate extends HollowObjectDelegate {

    public long getLastUpdated(int ordinal);

    public Long getLastUpdatedBoxed(int ordinal);

    public int getElementsOrdinal(int ordinal);

    public long getCharacterId(int ordinal);

    public Long getCharacterIdBoxed(int ordinal);

    public int getQuotesOrdinal(int ordinal);

    public CharacterTypeAPI getTypeAPI();

}