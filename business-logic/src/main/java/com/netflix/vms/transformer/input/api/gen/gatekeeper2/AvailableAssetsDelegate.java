package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface AvailableAssetsDelegate extends HollowObjectDelegate {

    public int getAvailableSubsOrdinal(int ordinal);

    public int getAvailableDubsOrdinal(int ordinal);

    public int getBlockedSubsOrdinal(int ordinal);

    public int getBlockedDubsOrdinal(int ordinal);

    public int getMissingSubsOrdinal(int ordinal);

    public int getMissingDubsOrdinal(int ordinal);

    public AvailableAssetsTypeAPI getTypeAPI();

}