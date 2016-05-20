package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface PersonVideoDelegate extends HollowObjectDelegate {

    public int getAliasIdsOrdinal(int ordinal);

    public int getRolesOrdinal(int ordinal);

    public long getPersonId(int ordinal);

    public Long getPersonIdBoxed(int ordinal);

    public PersonVideoTypeAPI getTypeAPI();

}