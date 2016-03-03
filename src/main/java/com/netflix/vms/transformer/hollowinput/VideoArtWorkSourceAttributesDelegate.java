package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoArtWorkSourceAttributesDelegate extends HollowObjectDelegate {

    public int getPassthroughOrdinal(int ordinal);

    public VideoArtWorkSourceAttributesTypeAPI getTypeAPI();

}