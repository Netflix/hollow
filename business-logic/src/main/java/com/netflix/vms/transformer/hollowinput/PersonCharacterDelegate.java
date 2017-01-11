package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface PersonCharacterDelegate extends HollowObjectDelegate {

    public long getPersonId(int ordinal);

    public Long getPersonIdBoxed(int ordinal);

    public long getCharacterId(int ordinal);

    public Long getCharacterIdBoxed(int ordinal);

    public PersonCharacterTypeAPI getTypeAPI();

}