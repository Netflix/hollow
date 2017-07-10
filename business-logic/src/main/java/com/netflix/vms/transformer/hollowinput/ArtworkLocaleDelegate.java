package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface ArtworkLocaleDelegate extends HollowObjectDelegate {

    public int getTerritoryCodesOrdinal(int ordinal);

    public int getBcp47CodeOrdinal(int ordinal);

    public int getEffectiveDateOrdinal(int ordinal);

    public int getAttributesOrdinal(int ordinal);

    public ArtworkLocaleTypeAPI getTypeAPI();

}