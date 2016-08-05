package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface ContractIdDelegate extends HollowObjectDelegate {

    public long getValue(int ordinal);

    public Long getValueBoxed(int ordinal);

    public ContractIdTypeAPI getTypeAPI();

}