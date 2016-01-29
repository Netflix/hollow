package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoDateWindowDelegate extends HollowObjectDelegate {

    public boolean getIsTheatricalRelease(int ordinal);

    public Boolean getIsTheatricalReleaseBoxed(int ordinal);

    public int getCountryCodeOrdinal(int ordinal);

    public long getStreetDate(int ordinal);

    public Long getStreetDateBoxed(int ordinal);

    public long getTheatricalReleaseYear(int ordinal);

    public Long getTheatricalReleaseYearBoxed(int ordinal);

    public long getTheatricalReleaseDate(int ordinal);

    public Long getTheatricalReleaseDateBoxed(int ordinal);

    public VideoDateWindowTypeAPI getTypeAPI();

}