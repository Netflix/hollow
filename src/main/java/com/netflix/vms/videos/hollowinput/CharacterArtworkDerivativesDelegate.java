package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface CharacterArtworkDerivativesDelegate extends HollowObjectDelegate {

    public int getRecipeNameOrdinal(int ordinal);

    public int getFileNameOrdinal(int ordinal);

    public long getImageId(int ordinal);

    public Long getImageIdBoxed(int ordinal);

    public int getCdnOriginServerIdOrdinal(int ordinal);

    public long getWidth(int ordinal);

    public Long getWidthBoxed(int ordinal);

    public int getCdnDirectoryOrdinal(int ordinal);

    public int getImageTypeOrdinal(int ordinal);

    public int getCdnOriginServerOrdinal(int ordinal);

    public long getHeight(int ordinal);

    public Long getHeightBoxed(int ordinal);

    public CharacterArtworkDerivativesTypeAPI getTypeAPI();

}