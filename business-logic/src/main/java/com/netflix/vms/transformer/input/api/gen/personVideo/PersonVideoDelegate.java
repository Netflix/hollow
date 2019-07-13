package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface PersonVideoDelegate extends HollowObjectDelegate {

    public int getAliasIdsOrdinal(int ordinal);

    public int getRolesOrdinal(int ordinal);

    public long getPersonId(int ordinal);

    public Long getPersonIdBoxed(int ordinal);

    public PersonVideoTypeAPI getTypeAPI();

}