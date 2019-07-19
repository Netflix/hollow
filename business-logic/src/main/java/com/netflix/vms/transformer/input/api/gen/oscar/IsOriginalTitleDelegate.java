package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface IsOriginalTitleDelegate extends HollowObjectDelegate {

    public boolean getValue(int ordinal);

    public Boolean getValueBoxed(int ordinal);

    public IsOriginalTitleTypeAPI getTypeAPI();

}