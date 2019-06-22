package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RightsDelegate extends HollowObjectDelegate {

    public int getWindowsOrdinal(int ordinal);

    public RightsTypeAPI getTypeAPI();

}