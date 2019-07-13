package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface IPLArtworkDerivativeDelegate extends HollowObjectDelegate {

    public String getRecipeName(int ordinal);

    public boolean isRecipeNameEqual(int ordinal, String testValue);

    public int getRecipeNameOrdinal(int ordinal);

    public int getWidthInPixels(int ordinal);

    public Integer getWidthInPixelsBoxed(int ordinal);

    public int getHeightInPixels(int ordinal);

    public Integer getHeightInPixelsBoxed(int ordinal);

    public int getTargetWidthInPixels(int ordinal);

    public Integer getTargetWidthInPixelsBoxed(int ordinal);

    public int getTargetHeightInPixels(int ordinal);

    public Integer getTargetHeightInPixelsBoxed(int ordinal);

    public String getRecipeDescriptor(int ordinal);

    public boolean isRecipeDescriptorEqual(int ordinal, String testValue);

    public int getRecipeDescriptorOrdinal(int ordinal);

    public String getCdnId(int ordinal);

    public boolean isCdnIdEqual(int ordinal, String testValue);

    public int getCdnIdOrdinal(int ordinal);

    public String getLanguageCode(int ordinal);

    public boolean isLanguageCodeEqual(int ordinal, String testValue);

    public int getLanguageCodeOrdinal(int ordinal);

    public int getModificationsOrdinal(int ordinal);

    public int getOverlayTypesOrdinal(int ordinal);

    public IPLArtworkDerivativeTypeAPI getTypeAPI();

}