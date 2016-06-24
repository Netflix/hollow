package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface ArtWorkImageTypeDelegate extends HollowObjectDelegate {

    public int getImageTypeOrdinal(int ordinal);

    public int getExtensionOrdinal(int ordinal);

    public int getRecipeOrdinal(int ordinal);

    public ArtWorkImageTypeTypeAPI getTypeAPI();

}