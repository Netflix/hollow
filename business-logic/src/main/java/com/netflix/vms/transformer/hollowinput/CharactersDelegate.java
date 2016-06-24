package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface CharactersDelegate extends HollowObjectDelegate {

    public long getId(int ordinal);

    public Long getIdBoxed(int ordinal);

    public int getPrefixOrdinal(int ordinal);

    public int getBOrdinal(int ordinal);

    public int getCnOrdinal(int ordinal);

    public CharactersTypeAPI getTypeAPI();

}