package com.netflix.vms.transformer.input.api.gen.exhibitDealAttribute;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface BooleanDelegate extends HollowObjectDelegate {

    public boolean getValue(int ordinal);

    public Boolean getValueBoxed(int ordinal);

    public BooleanTypeAPI getTypeAPI();

}