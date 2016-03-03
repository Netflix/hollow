package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface ArtworkAttributesDelegate extends HollowObjectDelegate {

    public int getPassthroughOrdinal(int ordinal);

    public ArtworkAttributesTypeAPI getTypeAPI();

}