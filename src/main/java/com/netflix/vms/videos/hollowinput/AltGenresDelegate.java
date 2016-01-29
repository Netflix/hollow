package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface AltGenresDelegate extends HollowObjectDelegate {

    public int getAlternateNamesOrdinal(int ordinal);

    public int getDisplayNameOrdinal(int ordinal);

    public long getAltGenreId(int ordinal);

    public Long getAltGenreIdBoxed(int ordinal);

    public int getShortNameOrdinal(int ordinal);

    public AltGenresTypeAPI getTypeAPI();

}