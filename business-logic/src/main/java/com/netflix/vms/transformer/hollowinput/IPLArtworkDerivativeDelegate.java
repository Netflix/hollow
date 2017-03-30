package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface IPLArtworkDerivativeDelegate extends HollowObjectDelegate {

    public int getRecipeNameOrdinal(int ordinal);

    public int getWidthInPixels(int ordinal);

    public Integer getWidthInPixelsBoxed(int ordinal);

    public int getHeightInPixels(int ordinal);

    public Integer getHeightInPixelsBoxed(int ordinal);

    public int getTargetWidthInPixels(int ordinal);

    public Integer getTargetWidthInPixelsBoxed(int ordinal);

    public int getTargetHeightInPixels(int ordinal);

    public Integer getTargetHeightInPixelsBoxed(int ordinal);

    public int getRecipeDescriptorOrdinal(int ordinal);

    public int getCdnIdOrdinal(int ordinal);

    public int getLanguageCodeOrdinal(int ordinal);

    public int getModificationsOrdinal(int ordinal);

    public int getOverlayTypesOrdinal(int ordinal);

    public IPLArtworkDerivativeTypeAPI getTypeAPI();

}