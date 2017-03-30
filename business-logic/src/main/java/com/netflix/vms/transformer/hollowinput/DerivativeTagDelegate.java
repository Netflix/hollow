package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface DerivativeTagDelegate extends HollowObjectDelegate {

    public String getValue(int ordinal);

    public boolean isValueEqual(int ordinal, String testValue);

    public DerivativeTagTypeAPI getTypeAPI();

}