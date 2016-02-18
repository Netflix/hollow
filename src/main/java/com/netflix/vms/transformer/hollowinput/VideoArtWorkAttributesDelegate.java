package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoArtWorkAttributesDelegate extends HollowObjectDelegate {

    public int getNameOrdinal(int ordinal);

    public int getValueOrdinal(int ordinal);

    public VideoArtWorkAttributesTypeAPI getTypeAPI();

}