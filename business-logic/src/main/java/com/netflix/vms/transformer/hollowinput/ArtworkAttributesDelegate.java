package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface ArtworkAttributesDelegate extends HollowObjectDelegate {

    public int getPassthroughOrdinal(int ordinal);

    public String getROLLOUT_EXCLUSIVE(int ordinal);

    public boolean isROLLOUT_EXCLUSIVEEqual(int ordinal, String testValue);

    public ArtworkAttributesTypeAPI getTypeAPI();

}