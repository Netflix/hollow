package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface ArtWorkImageTypeDelegate extends HollowObjectDelegate {

    public int getExtensionOrdinal(int ordinal);

    public int getRecipeOrdinal(int ordinal);

    public int getImageTypeOrdinal(int ordinal);

    public ArtWorkImageTypeTypeAPI getTypeAPI();

}