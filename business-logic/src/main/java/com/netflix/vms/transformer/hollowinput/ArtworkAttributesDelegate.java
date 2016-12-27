package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface ArtworkAttributesDelegate extends HollowObjectDelegate {

    public int getPassthroughOrdinal(int ordinal);

    public boolean getROLLOUT_EXCLUSIVE(int ordinal);

    public Boolean getROLLOUT_EXCLUSIVEBoxed(int ordinal);

    public ArtworkAttributesTypeAPI getTypeAPI();

}