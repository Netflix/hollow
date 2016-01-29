package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface RolloutPhasesElementsCharactersDelegate extends HollowObjectDelegate {

    public long getSequenceNumber(int ordinal);

    public Long getSequenceNumberBoxed(int ordinal);

    public long getRoleId(int ordinal);

    public Long getRoleIdBoxed(int ordinal);

    public long getPersonId(int ordinal);

    public Long getPersonIdBoxed(int ordinal);

    public long getCharacterId(int ordinal);

    public Long getCharacterIdBoxed(int ordinal);

    public RolloutPhasesElementsCharactersTypeAPI getTypeAPI();

}