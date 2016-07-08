package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface PersonsDelegate extends HollowObjectDelegate {

    public long getPersonId(int ordinal);

    public Long getPersonIdBoxed(int ordinal);

    public int getNameOrdinal(int ordinal);

    public int getBioOrdinal(int ordinal);

    public PersonsTypeAPI getTypeAPI();

}