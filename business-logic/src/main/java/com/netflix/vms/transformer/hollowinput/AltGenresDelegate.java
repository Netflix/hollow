package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface AltGenresDelegate extends HollowObjectDelegate {

    public long getAltGenreId(int ordinal);

    public Long getAltGenreIdBoxed(int ordinal);

    public int getDisplayNameOrdinal(int ordinal);

    public int getShortNameOrdinal(int ordinal);

    public int getAlternateNamesOrdinal(int ordinal);

    public AltGenresTypeAPI getTypeAPI();

}