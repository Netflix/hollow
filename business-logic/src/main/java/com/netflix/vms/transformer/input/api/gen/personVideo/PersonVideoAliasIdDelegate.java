package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface PersonVideoAliasIdDelegate extends HollowObjectDelegate {

    public int getValue(int ordinal);

    public Integer getValueBoxed(int ordinal);

    public PersonVideoAliasIdTypeAPI getTypeAPI();

}