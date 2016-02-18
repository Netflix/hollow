package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface PersonArtworkLocaleDelegate extends HollowObjectDelegate {

    public int getTerritoryCodesOrdinal(int ordinal);

    public int getBcp47CodeOrdinal(int ordinal);

    public int getEffectiveDateOrdinal(int ordinal);

    public PersonArtworkLocaleTypeAPI getTypeAPI();

}