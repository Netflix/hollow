package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoArtWorkSourceAttributesThemesDelegate extends HollowObjectDelegate {

    public int getValueOrdinal(int ordinal);

    public VideoArtWorkSourceAttributesThemesTypeAPI getTypeAPI();

}