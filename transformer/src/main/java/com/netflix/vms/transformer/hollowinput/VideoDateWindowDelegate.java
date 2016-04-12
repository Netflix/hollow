package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoDateWindowDelegate extends HollowObjectDelegate {

    public int getCountryCodeOrdinal(int ordinal);

    public boolean getIsTheatricalRelease(int ordinal);

    public Boolean getIsTheatricalReleaseBoxed(int ordinal);

    public long getStreetDate(int ordinal);

    public Long getStreetDateBoxed(int ordinal);

    public long getTheatricalReleaseDate(int ordinal);

    public Long getTheatricalReleaseDateBoxed(int ordinal);

    public int getTheatricalReleaseYear(int ordinal);

    public Integer getTheatricalReleaseYearBoxed(int ordinal);

    public VideoDateWindowTypeAPI getTypeAPI();

}