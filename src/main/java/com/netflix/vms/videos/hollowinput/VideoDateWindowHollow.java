package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoDateWindowHollow extends HollowObject {

    public VideoDateWindowHollow(VideoDateWindowDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public boolean _getIsTheatricalRelease() {
        return delegate().getIsTheatricalRelease(ordinal);
    }

    public Boolean _getIsTheatricalReleaseBoxed() {
        return delegate().getIsTheatricalReleaseBoxed(ordinal);
    }

    public StringHollow _getCountryCode() {
        int refOrdinal = delegate().getCountryCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getStreetDate() {
        return delegate().getStreetDate(ordinal);
    }

    public Long _getStreetDateBoxed() {
        return delegate().getStreetDateBoxed(ordinal);
    }

    public long _getTheatricalReleaseYear() {
        return delegate().getTheatricalReleaseYear(ordinal);
    }

    public Long _getTheatricalReleaseYearBoxed() {
        return delegate().getTheatricalReleaseYearBoxed(ordinal);
    }

    public long _getTheatricalReleaseDate() {
        return delegate().getTheatricalReleaseDate(ordinal);
    }

    public Long _getTheatricalReleaseDateBoxed() {
        return delegate().getTheatricalReleaseDateBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoDateWindowTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoDateWindowDelegate delegate() {
        return (VideoDateWindowDelegate)delegate;
    }

}