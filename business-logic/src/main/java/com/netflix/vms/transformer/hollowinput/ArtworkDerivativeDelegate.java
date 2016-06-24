package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface ArtworkDerivativeDelegate extends HollowObjectDelegate {

    public int getRecipeNameOrdinal(int ordinal);

    public int getCdnOriginServerIdOrdinal(int ordinal);

    public long getWidth(int ordinal);

    public Long getWidthBoxed(int ordinal);

    public int getCdnDirectoryOrdinal(int ordinal);

    public int getCdnIdOrdinal(int ordinal);

    public int getRecipeDescriptorOrdinal(int ordinal);

    public int getImageTypeOrdinal(int ordinal);

    public int getCdnOriginServerOrdinal(int ordinal);

    public long getHeight(int ordinal);

    public Long getHeightBoxed(int ordinal);

    public ArtworkDerivativeTypeAPI getTypeAPI();

}