package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface PersonAliasesDelegate extends HollowObjectDelegate {

    public long getAliasId(int ordinal);

    public Long getAliasIdBoxed(int ordinal);

    public int getNameOrdinal(int ordinal);

    public PersonAliasesTypeAPI getTypeAPI();

}